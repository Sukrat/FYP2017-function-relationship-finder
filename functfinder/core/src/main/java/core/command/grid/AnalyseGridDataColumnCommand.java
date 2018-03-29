package core.command.grid;


import com.arangodb.ArangoCursor;
import core.command.Command;
import core.command.CommandException;
import core.command.CommandProgess;
import core.model.CompiledRegression;
import core.model.Data;
import core.model.Regression;
import core.service.CsvService;
import core.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class AnalyseGridDataColumnCommand implements Command<AnalyseGridDataColumnCommand.Param, ByteArrayOutputStream> {

    private DataService dataService;

    private CsvService csvService;

    @Autowired
    public AnalyseGridDataColumnCommand(DataService dataService, CsvService csvService) {
        this.dataService = dataService;
        this.csvService = csvService;
    }

    @Override
    public ByteArrayOutputStream execute(CommandProgess progress, Param column) {
        Data any = dataService.findAny();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        int colNos = column.getColumnNos();
        if (colNos == 0) {
            throw new CommandException("Choose parameter column to be analysed cannot analyse output column!");
        } else if (colNos == -1) {
            List<Double> tolerances = new ArrayList<>(column.getTolerances());
            if (tolerances.size() == 1) {
                double tolerance = tolerances.get(0);
                // as one of the tolerances is already in the list
                for (int i = 2; i < any.getWorkColumns().size(); i++) {
                    tolerances.add(tolerance);
                }
            } else if (any.getWorkColumns().size() != tolerances.size()) {
                throw new CommandException(
                        "Number of tolerance must be equal to the data columns or enter one tolerance for all (expected: %d actual: %d)",
                        any.getWorkColumns().size(), tolerances.size());
            }
            return analyseAll(progress, any, tolerances);
        } else if (colNos < 0 || colNos >= any.getRawColumns().size()) {
            throw new CommandException(format("Column number doesnot exist! (Expected: < %d and > 0 and got: )",
                    any.getRawColumns().size(), column));
        }

        List<Double> tolerances = new ArrayList<>(column.getTolerances());
        if (tolerances.size() == 1) {
            double tolerance = tolerances.get(0);
            // as one of the tolerances is already in the list
            for (int i = 1; i < any.getWorkColumns().size() - 1; i++) {
                tolerances.add(tolerance);
            }
        } else if (any.getWorkColumns().size() - 1 != tolerances.size()) {
            throw new CommandException(
                    "Number of tolerance must be equal to the data columns or enter one tolerance for all (expected: %d actual: %d)",
                    any.getWorkColumns().size() - 1, tolerances.size());
        }

        ArangoCursor<Regression> regressions = analyseColumnByColNos(any, tolerances, colNos);

        return csvService.toCsv(regressions, true,
                new String[]{"colNo", "m1", "m2", "c1", "c2", "numOfDataPoints"},
                new CellProcessor[]{new NotNull(new ParseInt()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseLong())},
                (elem) -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("colNo", colNos);
                    map.put("m1", elem.getM1());
                    map.put("m2", elem.getM2());
                    map.put("c1", elem.getC1());
                    map.put("c2", elem.getC2());
                    map.put("numOfDataPoints", elem.getNumOfDataPoints());
                    return map;
                });
    }

    private ByteArrayOutputStream analyseAll(CommandProgess progress, Data sample, List<Double> tolerances) {
        List<Integer> colNos = new ArrayList<>();
        for (int i = 1; i < sample.getWorkColumns().size(); i++) {
            colNos.add(i);
        }

        progress.update(0, colNos.size(), "Analysing all columns!");
        AtomicInteger done = new AtomicInteger(0);
        List<CompiledRegression> compiledRegressions = colNos.parallelStream()
                .map(colNo -> {
                    ArangoCursor<Regression> regressions = analyseColumnByColNos(sample, tolerances, colNo);
                    progress.update(done.incrementAndGet(), colNos.size(), "%s col nos has been analysed!", colNo);
                    return CompiledRegression.compiledRegression(colNo, regressions);
                })
                .collect(Collectors.toList());

        progress.update("Compiling data together!");
        return csvService.toCsv(compiledRegressions, true,
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

    private ArangoCursor<Regression> analyseColumnByColNos(Data sample, List<Double> tolerances, int colNos) {
        // ax + b = y where a is a1/a2 and b is b1/b2
        String rawQuery = dataService.join(
                "FOR r IN @@col",
                "COLLECT index = [ %1$s ]",
                "INTO members = r",
                "FILTER COUNT(members) > 1",
                "let v = ( FOR elem IN members",
                "COLLECT AGGREGATE",
                format("sX = SUM(elem.workColumns.%s),", Data.colName(colNos)),
                format("sY = SUM(elem.workColumns.%s),", Data.colName(0)),
                format("sXX = SUM(POW(elem.workColumns.%s, 2)),", Data.colName(colNos)),
                format("sXY = SUM(elem.workColumns.%s * elem.workColumns.%s),", Data.colName(colNos), Data.colName(0)),
                "n = LENGTH(elem)",
                "return { sX: sX, sY: sY, sXX: sXX, sXY: sXY, n: n })[0]",
                "let a1 = (v.sX * v.sY) - (v.n * v.sXY)",
                "let a2 = pow(v.sX, 2) - (v.n * v.sXX)",
                "let b1 = (v.sX * v.sXY) - (v.sXX * v.sY)",
                "let b2 = pow(v.sX, 2) - (v.n * v.sXX)",
                "RETURN { numOfDataPoints: v.n, m1: a1, m2: a2, c1: b1, c2: b2 }");
        // ignoring first tolerance as that is for ouput column
        String cols = "";
        for (int i = 1; i < sample.getWorkColumns().size(); i++) {
            if (i == colNos) {
                continue;
            }
            cols += format("FLOOR(r.workColumns.%1$s / @tolerance%2$d),\n", Data.colName(i), i);
        }
        cols = cols.substring(0, cols.length() - 2);
        String query = format(rawQuery, cols);
        ArangoCursor<Regression> datas = dataService.query(query, new HashMap<String, Object>() {{
            // ignoring first tolerance as that is for ouput column
            int tolIndex = 0;
            for (int i = 1; i < sample.getWorkColumns().size(); i++) {
                if (i == colNos) {
                    continue;
                }
                put(format("tolerance%1$d", i), fixTolerance(tolerances.get(tolIndex++)));
            }
        }}, Regression.class);
        return datas;
    }

    private double fixTolerance(double tolerance) {
        return tolerance == 0.0 ? 1.0 : Math.abs(tolerance);
    }

    public static class Param {
        private int columnNos;
        private List<Double> tolerances;

        public Param(int columnNos, List<Double> tolerances) {
            this.columnNos = columnNos;
            this.tolerances = tolerances;
        }

        public int getColumnNos() {
            return columnNos;
        }

        public List<Double> getTolerances() {
            return tolerances;
        }
    }
}
