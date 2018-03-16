package functlyser.service;

import com.arangodb.ArangoCursor;
import functlyser.exception.ApiException;
import functlyser.model.CompiledRegression;
import functlyser.model.Data;
import functlyser.model.GridData;
import functlyser.model.Regression;
import functlyser.model.validator.DataValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.ArangoOperation;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class ScanService extends BaseSearchService {

    private ArangoOperation arangoOperation;

    private ValidatorRunner<DataValidator> dataValidator;

    private CsvService csvService;

    @Autowired
    public ScanService(ArangoOperation arangoOperation, ValidatorRunner<DataValidator> dataValidator, CsvService csvService) {
        this.arangoOperation = arangoOperation;
        this.dataValidator = dataValidator;
        this.csvService = csvService;
    }

    public Resource functionalCheck(double radius, double outputTolerance) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        List<String> columns = any.getColumns()
                .entrySet()
                .stream()
                .map(m -> format("columns.%s", m.getKey()))
                .collect(Collectors.toList());
        arangoOperation.ensureSkipListIndexMulti(Data.class, columns);

        int startIndex = 1;
        int endIndex = any.getColumns().size();

        StringBuilder builder = new StringBuilder();

        builder.append("LET result = (");
        builder.append("FOR r IN @@dataCollection\n");
        builder.append("LET neigh = (\n");
        builder.append("FOR ng in @@dataCollection\n");
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(format("FILTER (ng.columns.%1$s >= -@radius + r.columns.%1$s\n", Data.colName(i)));
            builder.append(format("&& ng.columns.%1$s <= @radius + r.columns.%1$s)\n", Data.colName(i)));
        }
        builder.append("LET dist = SQRT(\n");
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(format("POW(ng.columns.%1$s - r.columns.%1$s, 2)\n", Data.colName(i)));
            builder.append("+ \n");
        }
        builder.append("0 )\n");
        builder.append("FILTER dist <= @radius\n");
        builder.append("FILTER ABS(ng.columns.col0 - r.columns.col0) > @outputTolerance\n");
        builder.append("RETURN ng\n");
        builder.append(")\n");
        builder.append("RETURN neigh\n");
        builder.append(")\n");

        builder.append("FOR i IN FLATTEN(result)\n");
        builder.append("RETURN DISTINCT i\n");
        builder.append("\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@dataCollection", arangoOperation.collectionName(Data.class));
        bindVar.put("radius", Math.abs(radius));
        bindVar.put("outputTolerance", Math.abs(outputTolerance));
        ArangoCursor<Data> datas = arangoOperation.query(builder.toString(), bindVar, Data.class);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(any.getColumns().size());
        return csvService.convert(datas, false, params.getKey(), params.getValue(), (elem) -> {
            return elem.getColumns()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
        });
    }

    public Resource analyseParameter(double radius, int column) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }

        if (column == 0) {
            throw new ApiException("Choose parameter column to be analysed cannot analyse output column!");
        }

        List<String> columns = any.getColumns()
                .entrySet()
                .stream()
                .map(m -> format("columns.%s", m.getKey()))
                .collect(Collectors.toList());
        arangoOperation.ensureSkipListIndexMulti(Data.class, columns);

        if (column == -1) {
            return analyseAll(radius, any.getColumns().size());
        }

        if (column < 0 || column >= any.getColumns().size()) {
            throw new ApiException(format("Column number doesnot exist! (Expected: < %d and > 0 and got: )",
                    any.getColumns().size(), column));
        }

        ArangoCursor<Regression> regressions = analyseColumnByColNos(radius, column, any.getColumns().size());

        return csvService.convert(regressions, true,
                new String[]{"colNo", "m1", "m2", "c1", "c2", "numOfDataPoints"},
                new CellProcessor[]{new NotNull(new ParseInt()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseLong())},
                (elem) -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("colNo", column);

                    map.put("m1", elem.getM1());
                    map.put("m2", elem.getM2());

                    map.put("c1", elem.getC1());
                    map.put("c2", elem.getC2());

                    map.put("numOfDataPoints", elem.getNumOfDataPoints());
                    return map;
                });
    }

    private Resource analyseAll(double radius, int size) {
        List<Integer> colNos = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            colNos.add(i);
        }

        List<CompiledRegression> compiledRegressions = colNos.parallelStream()
                .map(colNo -> {
                    ArangoCursor<Regression> regressions = analyseColumnByColNos(radius, colNo, size);
                    return CompiledRegression.compiledRegression(colNo, regressions);
                })
                .collect(Collectors.toList());

        return csvService.convert(compiledRegressions, true,
                new String[]{"colNo", "meanM", "stdDevM", "weightedMeanM", "weightedStdDevM",
                        "meanC", "stdDevC", "weightedMeanC", "weightedStdDevC"},
                new CellProcessor[]{new NotNull(new ParseInt()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
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

                    map.put("weightedMeanM", elem.getWeightedMeanM());
                    map.put("weightedStdDevM", elem.getWeightedStdDevM());
                    map.put("weightedMeanC", elem.getWeightedMeanC());
                    map.put("weightedStdDevC", elem.getWeightedStdDevC());

                    return map;
                });
    }

    private ArangoCursor<Regression> analyseColumnByColNos(double radius, int column, int size) {
        int startIndex = 1;
        int endIndex = size;

        StringBuilder builder = new StringBuilder();
        builder.append("FOR r IN @@dataCollection\n");
        builder.append("LET v = (\n");
        builder.append("FOR ng in @@dataCollection\n");
        for (int i = startIndex; i < endIndex; i++) {
            if (i == column) {
                continue;
            }
            builder.append(format("FILTER (ng.columns.%1$s >= -@radius + r.columns.%1$s\n", Data.colName(i)));
            builder.append(format("&& ng.columns.%1$s <= @radius + r.columns.%1$s)\n", Data.colName(i)));
        }
        builder.append("LET dist = SQRT(\n");
        for (int i = startIndex; i < endIndex; i++) {
            if (i == column) {
                continue;
            }
            builder.append(format("POW(ng.columns.%1$s - r.columns.%1$s, 2)\n", Data.colName(i)));
            builder.append("+ \n");
        }
        builder.append("0 )\n");
        builder.append("FILTER dist <= @radius\n");
        builder.append("COLLECT AGGREGATE\n")
                .append(format("sX = SUM(ng.columns.%s),\n", Data.colName(column)))
                .append(format("sY = SUM(ng.columns.%s),\n", Data.colName(0))) //output column
                .append(format("sXX = SUM(POW(ng.columns.%s, 2)),\n", Data.colName(column)))
                .append(format("sXY = SUM(ng.columns.%s * ng.columns.%s),\n", Data.colName(column), Data.colName(0)))
                .append("n = LENGTH(ng)\n")
                .append("return { \n")
                .append("sX: sX,\n")
                .append("sY: sY,\n")
                .append("sXX: sXX,\n")
                .append("sXY: sXY,\n")
                .append("n: n\n")
                .append("})[0]\n");
        builder.append("FILTER v.n > 1\n");
        builder.append("LET a1 = (v.sX * v.sY) - (v.n * v.sXY)\n");
        builder.append("LET a2 = pow(v.sX, 2) - (v.n * v.sXX)\n");
        builder.append("LET b1 = (v.sX * v.sXY) - (v.sXX * v.sY)\n");
        builder.append("LET b2 = pow(v.sX, 2) - (v.n * v.sXX)\n");
        builder.append("RETURN {\n");
        builder.append("numOfDataPoints: v.n,\n");
        builder.append("m1: a1,\n");
        builder.append("m2: a2,\n");
        builder.append("c1: b1,\n");
        builder.append("c2: b2\n");
        builder.append("}\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@dataCollection", arangoOperation.collectionName(Data.class));
        bindVar.put("radius", radius);

        ArangoCursor<Regression> datas = arangoOperation.query(builder.toString(), bindVar, Regression.class);
        return datas;
    }

}
