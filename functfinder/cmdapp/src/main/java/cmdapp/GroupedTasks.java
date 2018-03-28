package cmdapp;

import cmdapp.argument.DatabaseArguments;
import cmdapp.argument.DbScanArguments;
import cmdapp.argument.GridArguments;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import core.arango.ArangoOperation;
import core.command.data.DataUploadCommand;
import core.service.CsvService;
import core.service.DataService;

public class GroupedTasks {

    public static String GRID_COMMAND = "grid";
    public static String DBSCAN_COMMAND = "dbscan";

    private String command;
    private DatabaseArguments databaseArguments;
    private GridArguments gridArguments;
    private DbScanArguments dbScanArguments;

    public GroupedTasks(String command,
                        DatabaseArguments databaseArguments,
                        GridArguments gridArguments,
                        DbScanArguments dbScanArguments) {
        this.command = command;
        this.databaseArguments = databaseArguments;
        this.gridArguments = gridArguments;
        this.dbScanArguments = dbScanArguments;
    }

    public void run() {
        ArangoDB build = new ArangoDB.Builder()
                .user(databaseArguments.getUser())
                .password(databaseArguments.getPassword())
                .host(databaseArguments.getHost(), databaseArguments.getPort())
                .maxConnections(databaseArguments.getMaxConnections())
                .build();
        String databaseName = databaseArguments.getDatabaseName();
        ArangoDatabase database = build.db(databaseName);
        try {
            database.getInfo();
        } catch (final ArangoDBException e) {
            if (new Integer(404).equals(e.getResponseCode())) {
                try {
                    build.createDatabase(databaseName);
                } catch (final ArangoDBException e1) {
                    throw e1;
                }
            } else {
                throw e;
            }
        }
        CsvService csvService = new CsvService();
        DataService dataService = new DataService(new ArangoOperation(database));
        if (GRID_COMMAND.equals(command)) {
            runGrid(csvService, dataService);
        } else if (DBSCAN_COMMAND.equals(command)) {
            runDbscan(csvService, dataService);
        }
    }

    private void runGrid(CsvService csvService, DataService dataService) {
//        new DataUploadCommand(csvService, dataService)
//                .execute(new CmdProgress(), new DataUploadCommand.Param())

    }

    private void runDbscan(CsvService csvService, DataService dataService) {

    }
}
