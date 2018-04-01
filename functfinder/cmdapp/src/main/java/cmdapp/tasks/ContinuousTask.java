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

    private ContinuousArguments continuousArguments;

    public ContinuousTask(ICommandExecutor commandExecutor, IDataService dataService, ICsvService csvService,
                          ContinuousArguments continuousArguments) {
        super(commandExecutor, dataService, csvService, continuousArguments);
        this.continuousArguments = continuousArguments;

        if (continuousArguments.getFromTol() > continuousArguments.getToTol()) {
            throw new CmdException("(fromtol or fromradius) value cannot be greater than (totol or toradius)");
        } else if (continuousArguments.getIncrement() <= 0.0) {
            throw new CmdException("increment tol value cannot be less than zero");
        }
    }

    @Override
    protected void afterInsertionRun() {
        ByteArrayOutputStream main = new ByteArrayOutputStream();
        for (Double i = continuousArguments.getFromTol(); i < continuousArguments.getToTol();
             i = Double.sum(i, continuousArguments.getIncrement())) {
            final Double tol = i;
            continuousArguments.getAnalyseColumns()
                    .stream()
                    .forEach(colNo -> {
                        Collection<CompiledRegression> compiledRegressions = null;

                        if (continuousArguments.isGridWay()) {
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
                                compiledRegressions
                        ));
                        try {
                            execute.writeTo(main);
                            execute.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        if (continuousArguments.isGridWay()) {
            String.format("grid-continuous-(%f)-(%f)-(%f).csv",
                    continuousArguments.getFromTol(),
                    continuousArguments.getIncrement(),
                    continuousArguments.getToTol());
        } else {
            String.format("dbscan-continuous-(%f)-(%f)-(%f).csv",
                    continuousArguments.getFromTol(),
                    continuousArguments.getIncrement(),
                    continuousArguments.getToTol());
        }
    }
}