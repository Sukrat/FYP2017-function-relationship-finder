package webapp.command.grid;


import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
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
import webapp.command.Command;
import webapp.command.CommandException;
import webapp.command.CommandProgess;
import webapp.model.CompiledRegression;
import webapp.model.Data;
import webapp.model.GridData;
import webapp.model.Regression;
import webapp.repository.DataRepository;
import webapp.repository.GridDataRepository;
import webapp.service.CsvService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class AnalyseGridDataColumnCommand implements Command<Integer, Resource> {

    private ArangoOperations operations;
    private DataRepository dataRepository;
    private GridDataRepository gridDataRepository;
    private CsvService csvService;

    @Autowired
    public AnalyseGridDataColumnCommand(ArangoOperations operations, DataRepository dataRepository,
                                        GridDataRepository gridDataRepository, CsvService csvService) {
        this.operations = operations;
        this.dataRepository = dataRepository;
        this.gridDataRepository = gridDataRepository;
        this.csvService = csvService;
    }

    @Override
    public Resource execute(CommandProgess progress, Integer column) {
        Data any = dataRepository.findFirstByWorkColumnsNotNull();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        if (gridDataRepository.count() == 0) {
            throw new CommandException("Data has not been grouped!");
        }
        if (column == 0) {
            throw new CommandException("Choose parameter column to be analysed cannot analyse output column!");
        }

        if (column == -1) {
            return analyseAll(progress, any);
        }

        if (column < 0 || column >= any.getRawColumns().size()) {
            throw new CommandException(format("Column number doesnot exist! (Expected: < %d and > 0 and got: )",
                    any.getRawColumns().size(), column));
        }

        ArangoCursor<Regression> regressions = analyseColumnByColNos(column);

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

    private Resource analyseAll(CommandProgess progress, Data sample) {
        List<Integer> colNos = new ArrayList<>();
        for (int i = 1; i < sample.getWorkColumns().size(); i++) {
            colNos.add(i);
        }

        progress.setTotalWork(colNos.size(), "Analysing all columns!");
        AtomicInteger done = new AtomicInteger(0);
        List<CompiledRegression> compiledRegressions = colNos.parallelStream()
                .map(colNo -> {
                    ArangoCursor<Regression> regressions = analyseColumnByColNos(colNo);
                    progress.update(done.incrementAndGet(), "%s col nos has been analysed!", colNo);
                    return CompiledRegression.compiledRegression(colNo, regressions);
                })
                .collect(Collectors.toList());

        progress.update("Compiling data together!");
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

    private ArangoCursor<Regression> analyseColumnByColNos(int column) {
        String query = "\n"
                + "FOR r IN @@gridCol\n"
                + "COLLECT index = APPEND(SLICE(r.boxIndex, 0, @col-1), SLICE(r.boxIndex, @col))\n"
                + "INTO list = r.members\n"
                + "let members = FLATTEN(list)\n"
                + "FILTER COUNT(members) > 1\n"
                + "let v = ( FOR member IN members\n"
                + "LET elem = FIRST(FOR d IN @@dataCol FILTER d._id == member LIMIT 1 RETURN d)\n"
                + "COLLECT AGGREGATE\n"
                + format("sX = SUM(elem.workColumns.%s),\n", Data.colName(column))
                + format("sY = SUM(elem.workColumns.%s),\n", Data.colName(0))
                + format("sXX = SUM(POW(elem.workColumns.%s, 2)),\n", Data.colName(column))
                + format("sXY = SUM(elem.workColumns.%s * elem.workColumns.%s),\n", Data.colName(column), Data.colName(0))
                + "n = LENGTH(elem)\n"
                + "return { sX: sX, sY: sY, sXX: sXX, sXY: sXY, n: n\n"
                + "})[0]\n" // ax + b = y where a is a1/a2 and b is b1/b2
                + "let a1 = (v.sX * v.sY) - (v.n * v.sXY)\n"
                + "let a2 = pow(v.sX, 2) - (v.n * v.sXX)\n"
                + "let b1 = (v.sX * v.sXY) - (v.sXX * v.sY)\n"
                + "let b2 = pow(v.sX, 2) - (v.n * v.sXX)\n"
                + "RETURN { numOfDataPoints: v.n, m1: a1, m2: a2, c1: b1, c2: b2 }\n";

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@gridCol", GridData.class);
        bindVar.put("@dataCol", Data.class);
        bindVar.put("col", column);
        ArangoCursor<Regression> datas = operations.query(query, bindVar, null, Regression.class);

        return datas;
    }
}
