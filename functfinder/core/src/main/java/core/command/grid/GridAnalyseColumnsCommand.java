package core.command.grid;

import com.arangodb.ArangoCursor;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.CompiledRegression;
import core.model.Data;
import core.model.Regression;
import core.service.IDataService;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class GridAnalyseColumnsCommand implements ICommand<Collection<CompiledRegression>> {

    private IDataService dataService;
    private List<Double> n1Tolerances;
    private Integer columnNo;

    public GridAnalyseColumnsCommand(IDataService dataService, List<Double> n1Tolerances, Integer columnNo) {
        this.dataService = dataService;
        this.n1Tolerances = n1Tolerances;
        this.columnNo = columnNo;
    }

    @Override
    public Collection<CompiledRegression> execute(IProgress progress) {
        Data any = dataService.findAny();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        List<Double> tolerances = new ArrayList<>(n1Tolerances);
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

        if (columnNo == 0) {
            throw new CommandException("Choose parameter column to be analysed cannot analyse output column!");
        } else if (columnNo == -1) {
            List<Integer> colNos = new ArrayList<>();
            for (int i = 1; i < any.getWorkColumns().size(); i++) {
                colNos.add(i);
            }
            progress.setWork(colNos.size(), "Analysing columns via grid!");
            return analyseAll(progress, any, tolerances, colNos);
        } else if (columnNo < 0 || columnNo >= any.getRawColumns().size()) {
            throw new CommandException(format("Column number doesnot exist! (Expected: < %d and > 0 and got: )",
                    any.getRawColumns().size(), columnNo));
        }
        progress.setWork(1, "Analysing column nos %d via grid!", columnNo);
        return analyseAll(progress, any, tolerances, Arrays.asList(columnNo));
    }

    private List<CompiledRegression> analyseAll(IProgress progress, Data sample, List<Double> tolerances, List<Integer> colNos) {
        progress.update(0, colNos.size(), "Analysing all columns!");
        Long totalPoints = dataService.count();
        List<CompiledRegression> compiledRegressions = colNos.parallelStream()
                .map(colNo -> {
                    ArangoCursor<Regression> regressions = analyseColumnByColNos(sample, tolerances, colNo);
                    progress.increment();
                    return CompiledRegression.compiledRegression(colNo, regressions, totalPoints, false);
                })
                .collect(Collectors.toList());

        progress.update("Compiling data together!");
        return compiledRegressions;
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
                format("sX = SUM(elem.rawColumns.%s),", Data.colName(colNos)),
                format("sY = SUM(elem.rawColumns.%s),", Data.colName(0)),
                format("sXX = SUM(POW(elem.rawColumns.%s, 2)),", Data.colName(colNos)),
                format("sXY = SUM(elem.rawColumns.%s * elem.rawColumns.%s),", Data.colName(colNos), Data.colName(0)),
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

}
