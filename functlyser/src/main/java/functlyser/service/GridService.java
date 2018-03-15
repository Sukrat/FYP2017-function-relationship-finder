package functlyser.service;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import functlyser.exception.ApiException;
import functlyser.model.CompiledRegression;
import functlyser.model.Data;
import functlyser.model.GridData;
import functlyser.model.Regression;
import functlyser.repository.ArangoOperation;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class GridService extends Service {

    private ArangoOperation arangoOperation;

    private CsvService csvService;

    @Autowired
    public GridService(ArangoOperation arangoOperation, CsvService csvService) {
        this.arangoOperation = arangoOperation;
        this.csvService = csvService;
    }

    public long cluster(List<Double> tolerances) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        int startIndex = 1;
        int endIndex = any.getColumns().size();
        int numParameterColumns = any.getColumns().size() - 1;

        List<Double> paramTolerances = new ArrayList<>(tolerances);
        if (paramTolerances.size() == 1) {
            double tolerance = paramTolerances.get(0);
            // as one of the tolerances is already in the list
            for (int i = startIndex + 1; i < endIndex; i++) {
                paramTolerances.add(tolerance);
            }
        }
        if (numParameterColumns != paramTolerances.size()) {
            throw new ApiException(
                    format("Number of tolerance must be equal to the data columns or enter one tolerance for all (expected: %d actual: %d)",
                            numParameterColumns, paramTolerances.size()));
        }

        // deleting all the records in grouped data
        arangoOperation.collection(GridData.class).truncate();

        StringBuilder builder = new StringBuilder();
        builder.append("FOR r IN @@collection\n");
        builder.append("COLLECT boxIndex = [ \n");
        // ignoring first tolerance as that is for ouput column
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(format("FLOOR(r.columns.%1$s / @tolerance%2$d)", Data.colName(i), i));
            if (i < endIndex - 1) {
                builder.append(", ");
            }
        }
        builder.append(" ] INTO members = r._id\n");
        builder.append("INSERT {\n");
        builder.append("boxIndex: boxIndex, \n");
        builder.append("members: members \n");
        builder.append("} INTO @@newCollection\n");
        builder.append("RETURN { count: 1}\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(Data.class));
        bindVar.put("@newCollection", arangoOperation.collectionName(GridData.class));
        // ignoring first tolerance as that is for ouput column
        for (int i = startIndex; i < endIndex; i++) {
            bindVar.put(format("tolerance%1$d", i), fixTolerance(paramTolerances.get(i - 1)));
        }

        ArangoCursor<BaseDocument> result = arangoOperation.query(builder.toString(), bindVar, BaseDocument.class);

        return result.asListRemaining().size();
    }

    public Resource functionalCheck(double tolerance) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        GridData anyGrouped = arangoOperation.findAny(GridData.class);
        if (anyGrouped == null) {
            throw new ApiException("Data has not been clustered!");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("LET result = FLATTEN(");
        builder.append("FOR r IN @@clusterCollection\n");
        builder.append("FILTER COUNT(r.members) > 1\n");
        builder.append("LET grouped_y = \n")
                .append("( FOR member IN r.members\n")
                .append("LET elem = FIRST(\n")
                .append("FOR d in @@dataCollection FILTER d._id == member LIMIT 1 RETURN d\n")
                .append(")\n")
                .append(format("COLLECT y = FLOOR(elem.columns.%s / @tolerance) INTO elems = elem\n", Data.colName(0)))
                .append("RETURN elems )\n");
        builder.append("FILTER COUNT(grouped_y) > 1\n");
        builder.append("RETURN FLATTEN(grouped_y)\n");
        builder.append(")\n");
        builder.append("FOR r in result\n");
        builder.append("RETURN r\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@clusterCollection", arangoOperation.collectionName(GridData.class));
        bindVar.put("@dataCollection", arangoOperation.collectionName(Data.class));
        bindVar.put("tolerance", fixTolerance(tolerance));
        ArangoCursor<Data> datas = arangoOperation.query(builder.toString(), bindVar, Data.class);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(any.getColumns().size());
        return csvService.convert(datas, false, params.getKey(), params.getValue(), (elem) -> {
            return elem.getColumns()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
        });
    }

    public Resource analyseParameter(int column) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        GridData anyGrouped = arangoOperation.findAny(GridData.class);
        if (anyGrouped == null) {
            throw new ApiException("Data has not been grouped!");
        }
        if (column == 0) {
            throw new ApiException("Choose parameter column to be analysed cannot analyse output column!");
        }

        if (column == -1) {
            return analyseAll(any);
        }

        if (column < 0 || column >= any.getColumns().size()) {
            throw new ApiException(format("Column number doesnot exist! (Expected: < %d and > 0 and got: )",
                    any.getColumns().size(), column));
        }

        ArangoCursor<Regression> regressions = analyseColumnByColNos(column);

        return csvService.convert(regressions, true,
                new String[]{"colNo", "m1", "m2", "c1", "c2"},
                new CellProcessor[]{new NotNull(new ParseInt()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble())},
                (elem) -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("colNo", column);
                    map.put("m1", elem.getM1());
                    map.put("m2", elem.getM2());
                    map.put("c1", elem.getC1());
                    map.put("c2", elem.getC2());
                    return map;
                });
    }

    private Resource analyseAll(Data sample) {
        List<Integer> colNos = new ArrayList<>();
        for (int i = 1; i < sample.getColumns().size(); i++) {
            colNos.add(i);
        }

        List<CompiledRegression> compiledRegressions = colNos.parallelStream()
                .map(colNo -> {
                    ArangoCursor<Regression> regressions = analyseColumnByColNos(colNo);
                    return CompiledRegression.compiledRegression(colNo, regressions);
                })
                .collect(Collectors.toList());

        return csvService.convert(compiledRegressions, true,
                new String[]{"colNo", "meanM", "stdDevM", "meanC", "stdDevC"},
                new CellProcessor[]{new NotNull(new ParseInt()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble())},
                (elem) -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("colNo", elem.getColNo());
                    map.put("meanM", elem.getMeanM());
                    map.put("stdDevM", elem.getStdDevM());
                    map.put("meanC", elem.getMeanC());
                    map.put("stdDevC", elem.getStdDevC());
                    return map;
                });
    }

    private ArangoCursor<Regression> analyseColumnByColNos(int column) {
        StringBuilder builder = new StringBuilder();
        builder.append("FOR r IN @@clusterCollection\n");
        builder.append("COLLECT index = APPEND(SLICE(r.boxIndex, 0, @col-1), SLICE(r.boxIndex, @col))\n");
        builder.append("INTO list = r.members\n");
        builder.append("let members = FLATTEN(list)\n");
        builder.append("FILTER COUNT(members) > 1\n");
        builder.append("let v = ( FOR member IN members\n")
                .append("LET elem = FIRST(\n")
                .append("FOR d IN @@dataCollection FILTER d._id == member LIMIT 1 RETURN d\n")
                .append(")\n")
                .append("\n")
                .append("COLLECT AGGREGATE\n")
                .append(format("sX = SUM(elem.columns.%s),\n", Data.colName(column)))
                .append(format("sY = SUM(elem.columns.%s),\n", Data.colName(0))) //output column
                .append(format("sXX = SUM(POW(elem.columns.%s, 2)),\n", Data.colName(column)))
                .append(format("sXY = SUM(elem.columns.%s * elem.columns.%s),\n", Data.colName(column), Data.colName(0)))
                .append("n = LENGTH(elem)\n")
                .append("return { \n")
                .append("sX: sX,\n")
                .append("sY: sY,\n")
                .append("sXX: sXX,\n")
                .append("sXY: sXY,\n")
                .append("n: n\n")
                .append("})[0]\n");
        // ax + b = y where a is a1/a2 and b is b1/b2
        builder.append("let a1 = (v.sX * v.sY) - (v.n * v.sXY)\n");
        builder.append("let a2 = pow(v.sX, 2) - (v.n * v.sXX)\n");
        builder.append("let b1 = (v.sX * v.sXY) - (v.sXX * v.sY)\n");
        builder.append("let b2 = pow(v.sX, 2) - (v.n * v.sXX)\n");
        builder.append("RETURN {\n");
        builder.append("m1: a1,\n");
        builder.append("m2: a2,\n");
        builder.append("c1: b1,\n");
        builder.append("c2: b2\n");
        builder.append("}\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@clusterCollection", arangoOperation.collectionName(GridData.class));
        bindVar.put("@dataCollection", arangoOperation.collectionName(Data.class));
        bindVar.put("col", column);
        ArangoCursor<Regression> datas = arangoOperation.query(builder.toString(), bindVar, Regression.class);

        return datas;
    }

    private double fixTolerance(double tolerance) {
        return tolerance == 0.0 ? 1.0 : Math.abs(tolerance);
    }

    private Pair<String[], CellProcessor[]> getArgumentsForCsv(int size) {
        CellProcessor[] processors = new CellProcessor[size];
        String[] headers = new String[size];
        for (int i = 0; i < size; i++) {
            headers[i] = format("%s%d", Data.prefixColumn, i);
            processors[i] = new NotNull(new ParseDouble());
        }
        return new Pair<>(headers, processors);
    }
}
