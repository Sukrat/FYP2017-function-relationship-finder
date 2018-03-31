package cmdapp.tasks;


import cmdapp.CmdCommandExecutor;
import cmdapp.CmdProgress;
import cmdapp.argument.DbScanArguments;
import com.arangodb.ArangoCursor;
import core.command.csv.CompiledRegressionToCsvCommand;
import core.command.csv.DataToCsvCommand;
import core.command.dbscan.DbScanAnalyseColumnCommand;
import core.command.dbscan.DbScanAnalyseColumnsCommand;
import core.command.dbscan.DbScanFunctionalCheckCommand;
import core.command.dbscan.DbScanFunctionalCommand;
import core.model.CompiledRegression;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collection;

@Component
public class DbscanTask extends Task {

    private DbScanArguments dbScanArguments;

    public DbscanTask(CmdCommandExecutor cmdCommandExecutor, DataService dataService, CsvService csvService,
                      DbScanArguments dbScanArguments) {
        super(cmdCommandExecutor, dataService, csvService);
        this.dbScanArguments = dbScanArguments;
    }

    @Override
    public void run() {
        // inserting files
        insert(dbScanArguments);

        if (dbScanArguments.isFunctionCheck()) {
            ArangoCursor<Data> datas = cmdCommandExecutor.execute(new DbScanFunctionalCommand(
                    dataService,
                    dbScanArguments.getRadius(),
                    dbScanArguments.getOutputRadius()
            ));
            ByteArrayOutputStream execute = cmdCommandExecutor.execute(new DataToCsvCommand(
                    csvService,
                    datas.asListRemaining()
            ));
            save(execute, "dbscan-function-check.csv");
        }

        dbScanArguments.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    Collection<CompiledRegression> compiledRegressions = cmdCommandExecutor.execute(new DbScanAnalyseColumnsCommand(
                            dataService,
                            dbScanArguments.getRadius(),
                            columNo
                    ));
                    ByteArrayOutputStream execute = cmdCommandExecutor.execute(new CompiledRegressionToCsvCommand(
                            csvService,
                            compiledRegressions
                    ));
                    save(execute, String.format("dbscan-analyse-%d.csv", columNo));
                });

        cleanup();
    }
}
