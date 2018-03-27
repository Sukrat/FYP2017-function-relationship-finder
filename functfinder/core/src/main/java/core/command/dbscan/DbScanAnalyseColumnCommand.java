package core.command.dbscan;

import com.arangodb.ArangoCursor;
import core.command.Command;
import core.command.CommandException;
import core.command.CommandProgess;
import core.model.CompiledRegression;
import core.model.Data;
import core.model.Regression;
import core.service.CsvService;
import core.service.DataService;
import javafx.util.Pair;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DbScanAnalyseColumnCommand implements Command<DbScanAnalyseColumnCommand.Param, ByteArrayOutputStream> {

    private DataService dataService;
    private CsvService csvService;

    public DbScanAnalyseColumnCommand(DataService dataService, CsvService csvService) {
        this.dataService = dataService;
        this.csvService = csvService;
    }

    @Override
    public ByteArrayOutputStream execute(CommandProgess progress, Param param) {
        Data any = dataService.findAny();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }

        int columnNo = param.getColumnNo();
        if (columnNo == 0) {
            throw new CommandException("Choose parameter column to be analysed cannot analyse output columnNo!");
        }

        List<String> columns = any.getWorkColumns()
                .entrySet()
                .stream()
                .map(m -> format("workColumns.%s", m.getKey()))
                .collect(Collectors.toList());

        columns.parallelStream()
                .forEach((column) -> {
                    dataService.ensureSkipListIndex(Arrays.asList(column));
                });

        if (columnNo == -1) {
            return analyseAll(progress, param.getRadius(), any.getRawColumns().size());
        }

        if (columnNo < 0 || columnNo >= any.getRawColumns().size()) {
            throw new CommandException("Column number doesnot exist! (Expected: < %d and > 0 and got: )",
                    any.getRawColumns().size(), columnNo);
        }

        ArangoCursor<Regression> regressions = analyseColumnByColNos(param.getRadius(), columnNo, any.getRawColumns().size());

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
                    map.put("colNo", columnNo);

                    map.put("m1", elem.getM1());
                    map.put("m2", elem.getM2());

                    map.put("c1", elem.getC1());
                    map.put("c2", elem.getC2());

                    map.put("numOfDataPoints", elem.getNumOfDataPoints());
                    return map;
                });
    }

    private ByteArrayOutputStream analyseAll(CommandProgess progress, double radius, int size) {
        List<Integer> colNos = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            colNos.add(i);
        }
        progress.update(0, colNos.size(), "Analysing all columns!");
        AtomicInteger done = new AtomicInteger(0);
        List<CompiledRegression> compiledRegressions = colNos.parallelStream()
                .map(colNo -> {
                    ArangoCursor<Regression> regressions = analyseColumnByColNos(radius, colNo, size);
                    progress.update(done.incrementAndGet(), colNos.size(), "%s col nos has been analysed!", colNo);
                    return CompiledRegression.compiledRegression(colNo, regressions);
                })
                .collect(Collectors.toList());

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

    private ArangoCursor<Regression> analyseColumnByColNos(double radius, int column, int size) {
        int startIndex = 1;
        int endIndex = size;

        String rawQuery = "FOR r IN @@col\n"
                + "LET v = (\n"
                + "FOR ng in @@col\n"
                + "%1$s\n"
                + "LET dist = SQRT( %2$s )\n"
                + "FILTER dist <= @radius\n"
                + "COLLECT AGGREGATE\n"
                + format("sX = SUM(ng.workColumns.%s),\n", Data.colName(column))
                + format("sY = SUM(ng.workColumns.%s),\n", Data.colName(0)) + //output column
                format("sXX = SUM(POW(ng.workColumns.%s, 2)),\n", Data.colName(column))
                + format("sXY = SUM(ng.workColumns.%s * ng.workColumns.%s),\n", Data.colName(column), Data.colName(0))
                + "n = LENGTH(ng)\n"
                + "return { sX: sX, sY: sY, sXX: sXX, sXY: sXY, n: n })[0]\n"
                + "FILTER v.n > 1\n"
                + "LET a1 = (v.sX * v.sY) - (v.n * v.sXY)\n"
                + "LET a2 = pow(v.sX, 2) - (v.n * v.sXX)\n"
                + "LET b1 = (v.sX * v.sXY) - (v.sXX * v.sY)\n"
                + "LET b2 = pow(v.sX, 2) - (v.n * v.sXX)\n"
                + "RETURN {\n"
                + "numOfDataPoints: v.n,\n"
                + "m1: a1,\n"
                + "m2: a2,\n"
                + "c1: b1,\n"
                + "c2: b2\n"
                + "}\n";

        String filter = "";
        String dist = "";
        for (int i = startIndex; i < endIndex; i++) {
            if (i == column) {
                continue;
            }
            filter += format("FILTER (ng.workColumns.%1$s >= -@radius + r.workColumns.%1$s\n", Data.colName(i));
            filter += format("&& ng.workColumns.%1$s <= @radius + r.workColumns.%1$s)\n", Data.colName(i));
            dist += format("POW(ng.workColumns.%1$s - r.workColumns.%1$s, 2) + \n", Data.colName(i));
        }
        dist += "0";

        String query = format(rawQuery, filter, dist);
        ArangoCursor<Regression> datas = dataService.query(query, new HashMap<String, Object>() {{
            put("radius", radius);
        }}, Regression.class);
        return datas;
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

    public static class Param {
        private Double radius;
        private Integer columnNo;

        public Param(Double radius, Integer columnNo) {
            this.radius = radius;
            this.columnNo = columnNo;
        }

        public Double getRadius() {
            return radius;
        }

        public Integer getColumnNo() {
            return columnNo;
        }
    }

}
