package cmdapp.tasks;

import cmdapp.CommandExecutor;
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
public class GridTask extends Task {

    private GridArguments gridArguments;

    public GridTask(ICommandExecutor commandExecutor, IDataService dataService, ICsvService csvService,
                    GridArguments gridArguments) {
        super(commandExecutor, dataService, csvService);
        this.gridArguments = gridArguments;
    }

    @Override
    public void run() {
        // inserting files
        insert(gridArguments);

        if (gridArguments.isFunctionCheck()) {
            ArangoCursor<Data> datas = executor.execute(new GridFunctionCommand(
                    dataService,
                    gridArguments.getParameterTolerances(),
                    gridArguments.getOutputTolerances()
            ));
            ByteArrayOutputStream execute = executor.execute(new DataToCsvCommand(
                    csvService,
                    datas.asListRemaining()
            ));
            save(execute, "grid-function-check.csv");
        }

        gridArguments.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    Collection<CompiledRegression> compiledRegressions = executor.execute(new GridAnalyseColumnsCommand(
                            dataService,
                            gridArguments.getParameterTolerances(),
                            columNo
                    ));
                    ByteArrayOutputStream execute = executor.execute(new CompiledRegressionToCsvCommand(
                            csvService,
                            compiledRegressions
                    ));
                    save(execute, String.format("grid-analyse-%d.csv", columNo));
                });
        cleanup();
    }
}
