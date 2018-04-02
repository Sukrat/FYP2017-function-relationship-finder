package cmdapp.tasks;

import cmdapp.argument.GridArguments;
import com.arangodb.ArangoCursor;
import core.command.ICommandExecutor;
import core.command.csv.CompiledRegressionToCsvCommand;
import core.command.csv.DataToCsvCommand;
import core.command.grid.GridAnalyseColumnsCommand;
import core.command.grid.GridFunctionCommand;
import core.model.CompiledRegression;
import core.model.Data;
import core.service.ICsvService;
import core.service.IDataService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collection;

@Component
public class GridTask extends ExecutionTask {

    private GridArguments args;

    public GridTask(ICommandExecutor commandExecutor, IDataService dataService, ICsvService csvService,
                    GridArguments args) {
        super(commandExecutor, dataService, csvService, args);
        this.args = args;

    }

    @Override
    protected void afterInsertionRun() {
        System.out.printf("\nDbscan analyse started with radius: %s, cols: %s, %s!",
                args.getParameterTolerances().toString(),
                args.getAnalyseColumns().toString(), args.isNormalise() ? "with normalization" : ""
        );
        if (args.isFunctionCheck()) {
            ArangoCursor<Data> datas = executor.execute(new GridFunctionCommand(
                    dataService,
                    args.getParameterTolerances(),
                    args.getOutputTolerances()
            ));
            ByteArrayOutputStream execute = executor.execute(new DataToCsvCommand(
                    csvService,
                    datas.asListRemaining()
            ));
            save(execute, "grid-fc.csv");
        }
        args.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    Collection<CompiledRegression> compiledRegressions = executor.execute(new GridAnalyseColumnsCommand(
                            dataService,
                            args.getParameterTolerances(),
                            columNo
                    ));
                    ByteArrayOutputStream execute = executor.execute(new CompiledRegressionToCsvCommand(
                            csvService,
                            compiledRegressions
                    ));
                    save(execute, String.format("grid-analyse-(%d).csv", columNo));
                });
    }
}
