package cmdapp.tasks;

import cmdapp.CmdProgress;
import cmdapp.argument.GridArguments;
import core.command.data.*;
import core.command.grid.AnalyseGridDataColumnCommand;
import core.command.grid.GridFunctionCheckCommand;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class GridTask extends Task {

    private DataUploadCommand dataUploadCommand;
    private DeleteDataCommand deleteDataCommand;
    private ListFileNamesCommand listFileNamesCommand;
    private GridFunctionCheckCommand gridFunctionCheckCommand;
    private AnalyseGridDataColumnCommand analyseGridDataColumnCommand;
    private GridArguments gridArguments;
    private NormalizeCommand normalizeCommand;

    public GridTask(DataUploadCommand dataUploadCommand,
                    DeleteDataCommand deleteDataCommand,
                    ListFileNamesCommand listFileNamesCommand,
                    GridFunctionCheckCommand gridFunctionCheckCommand,
                    AnalyseGridDataColumnCommand analyseGridDataColumnCommand,
                    GridArguments gridArguments, NormalizeCommand normalizeCommand) {
        this.dataUploadCommand = dataUploadCommand;
        this.deleteDataCommand = deleteDataCommand;
        this.listFileNamesCommand = listFileNamesCommand;
        this.gridFunctionCheckCommand = gridFunctionCheckCommand;
        this.analyseGridDataColumnCommand = analyseGridDataColumnCommand;
        this.gridArguments = gridArguments;
        this.normalizeCommand = normalizeCommand;
    }

    @Override
    public void run() {
        // inserting files
        insert(gridArguments, dataUploadCommand, normalizeCommand);

        if (gridArguments.isFunctionCheck()) {
            ByteArrayOutputStream execute = gridFunctionCheckCommand.execute(new CmdProgress(),
                    new GridFunctionCheckCommand.Param(gridArguments.getOutputTolerances(),
                            gridArguments.getParameterTolerances()));
            save(execute, "grid-function-check.csv");
        }

        gridArguments.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    ByteArrayOutputStream analyse = analyseGridDataColumnCommand.execute(new CmdProgress(),
                            new AnalyseGridDataColumnCommand.Param(columNo, gridArguments.getParameterTolerances()));
                    save(analyse, String.format("grid-analyse-%d.csv", columNo));
                });

        cleanup(listFileNamesCommand, deleteDataCommand);
    }
}
