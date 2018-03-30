package cmdapp.tasks;


import cmdapp.CmdProgress;
import cmdapp.argument.DbScanArguments;
import core.command.data.DataUploadCommand;
import core.command.data.DeleteDataCommand;
import core.command.data.ListFileNamesCommand;
import core.command.data.NormalizeCommand;
import core.command.dbscan.DbScanAnalyseColumnCommand;
import core.command.dbscan.DbScanFunctionalCheckCommand;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class DbscanTask extends Task {

    private DataUploadCommand dataUploadCommand;
    private DeleteDataCommand deleteDataCommand;
    private ListFileNamesCommand listFileNamesCommand;
    private DbScanFunctionalCheckCommand dbScanFunctionalCheckCommand;
    private DbScanAnalyseColumnCommand dbScanAnalyseColumnCommand;
    private DbScanArguments dbScanArguments;
    private NormalizeCommand normalizeCommand;

    public DbscanTask(DataUploadCommand dataUploadCommand,
                      DeleteDataCommand deleteDataCommand,
                      ListFileNamesCommand listFileNamesCommand,
                      DbScanFunctionalCheckCommand dbScanFunctionalCheckCommand,
                      DbScanAnalyseColumnCommand dbScanAnalyseColumnCommand,
                      DbScanArguments dbScanArguments, NormalizeCommand normalizeCommand) {
        this.dataUploadCommand = dataUploadCommand;
        this.deleteDataCommand = deleteDataCommand;
        this.listFileNamesCommand = listFileNamesCommand;
        this.dbScanFunctionalCheckCommand = dbScanFunctionalCheckCommand;
        this.dbScanAnalyseColumnCommand = dbScanAnalyseColumnCommand;
        this.dbScanArguments = dbScanArguments;
        this.normalizeCommand = normalizeCommand;
    }

    @Override
    public void run() {
        // inserting files
        insert(dbScanArguments, dataUploadCommand, normalizeCommand);

        if (dbScanArguments.isFunctionCheck()) {
            ByteArrayOutputStream execute = dbScanFunctionalCheckCommand.execute(new CmdProgress(),
                    new DbScanFunctionalCheckCommand.Param(dbScanArguments.getRadius(),
                            dbScanArguments.getOutputRadius()));
            save(execute, "dbscan-function-check.csv");
        }

        dbScanArguments.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    ByteArrayOutputStream analyse = dbScanAnalyseColumnCommand.execute(new CmdProgress(),
                            new DbScanAnalyseColumnCommand.Param(dbScanArguments.getOutputRadius(), columNo));
                    save(analyse, String.format("dbscan-analyse-%d.csv", columNo));
                });

        cleanup(listFileNamesCommand, deleteDataCommand);
    }
}
