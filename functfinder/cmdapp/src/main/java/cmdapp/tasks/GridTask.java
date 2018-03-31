package cmdapp.tasks;

import cmdapp.CmdCommandExecutor;
import cmdapp.CmdProgress;
import cmdapp.argument.DbScanArguments;
import cmdapp.argument.GridArguments;
import com.arangodb.ArangoCursor;
import core.command.csv.CompiledRegressionToCsvCommand;
import core.command.csv.DataToCsvCommand;
import core.command.grid.AnalyseGridDataColumnCommand;
import core.command.grid.GridAnalyseColumnsCommand;
import core.command.grid.GridFunctionCheckCommand;
import core.command.grid.GridFunctionCommand;
import core.model.CompiledRegression;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collection;

@Component
public class GridTask extends Task {

    private GridArguments gridArguments;

    public GridTask(CmdCommandExecutor cmdCommandExecutor, DataService dataService, CsvService csvService,
                    GridArguments gridArguments) {
        super(cmdCommandExecutor, dataService, csvService);
        this.gridArguments = gridArguments;
    }

    @Override
    public void run() {
        // inserting files
        insert(gridArguments);

        if (gridArguments.isFunctionCheck()) {
            ArangoCursor<Data> datas = cmdCommandExecutor.execute(new GridFunctionCommand(
                    dataService,
                    gridArguments.getParameterTolerances(),
                    gridArguments.getOutputTolerances()
            ));
            ByteArrayOutputStream execute = cmdCommandExecutor.execute(new DataToCsvCommand(
                    csvService,
                    datas.asListRemaining()
            ));
            save(execute, "grid-function-check.csv");
        }

        gridArguments.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    Collection<CompiledRegression> compiledRegressions = cmdCommandExecutor.execute(new GridAnalyseColumnsCommand(
                            dataService,
                            gridArguments.getParameterTolerances(),
                            columNo
                    ));
                    ByteArrayOutputStream execute = cmdCommandExecutor.execute(new CompiledRegressionToCsvCommand(
                            csvService,
                            compiledRegressions
                    ));
                    save(execute, String.format("grid-analyse-%d.csv", columNo));
                });
        cleanup();
    }
}
