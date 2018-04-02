package cmdapp.tasks;

import cmdapp.CmdException;
import cmdapp.argument.ContinuousArguments;
import core.command.ICommandExecutor;
import core.command.csv.CompiledRegressionToCsvCommand;
import core.command.dbscan.DbScanAnalyseColumnsCommand;
import core.command.grid.GridAnalyseColumnsCommand;
import core.model.CompiledRegression;
import core.service.ICsvService;
import core.service.IDataService;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@Component
public class ContinuousTask extends ExecutionTask {

    private ContinuousArguments args;

    public ContinuousTask(ICommandExecutor commandExecutor, IDataService dataService, ICsvService csvService,
                          ContinuousArguments args) {
        super(commandExecutor, dataService, csvService, args);
        this.args = args;

        if (args.getFromTol() > args.getToTol()) {
            throw new CmdException("(fromtol or fromradius) value cannot be greater than (totol or toradius)");
        } else if (args.getIncrement() <= 0.0) {
            throw new CmdException("increment tol value cannot be less than zero");
        }
    }

    @Override
    protected void afterInsertionRun() {
        System.out.printf("\nContinuous %s analyse started from tol: %f, increment: %f, to: %f %s!",
                args.isGridWay() ? "grid" : "dbscan", args.getFromTol(), args.getIncrement(), args.getToTol(),
                args.isNormalise() ? "with normalization" : ""
        );
        ByteArrayOutputStream main = new ByteArrayOutputStream();
        int count = (int) ((args.getToTol() - args.getFromTol()) / args.getIncrement());
        for (int i = 0; i < count; i++) {
            final int n = i;
            final Double tol = args.getFromTol() + (i * args.getIncrement());

            args.getAnalyseColumns()
                    .stream()
                    .forEach(colNo -> {
                        Collection<CompiledRegression> compiledRegressions = null;

                        if (args.isGridWay()) {
                            compiledRegressions = executor.execute(new GridAnalyseColumnsCommand(
                                    dataService,
                                    Arrays.asList(tol),
                                    colNo
                            ));
                        } else {
                            compiledRegressions = executor.execute(new DbScanAnalyseColumnsCommand(
                                    dataService,
                                    tol,
                                    colNo
                            ));
                        }

                        ByteArrayOutputStream execute = executor.execute(new CompiledRegressionToCsvCommand(
                                csvService,
                                compiledRegressions,
                                n == 0
                        ));
                        try {
                            execute.writeTo(main);
                            execute.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        String filename;
        if (args.isGridWay()) {
            filename = String.format("grid-continuous-(%f)-(%f)-(%f).csv",
                    args.getFromTol(),
                    args.getIncrement(),
                    args.getToTol());
        } else {
            filename = String.format("dbscan-continuous-(%f)-(%f)-(%f).csv",
                    args.getFromTol(),
                    args.getIncrement(),
                    args.getToTol());
        }
        save(main, filename);
    }
}