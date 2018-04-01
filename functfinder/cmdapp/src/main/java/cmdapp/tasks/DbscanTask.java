package cmdapp.tasks;


import cmdapp.CommandExecutor;
import cmdapp.argument.DbScanArguments;
import com.arangodb.ArangoCursor;
import core.command.ICommandExecutor;
import core.command.csv.CompiledRegressionToCsvCommand;
import core.command.csv.DataToCsvCommand;
import core.command.dbscan.DbScanAnalyseColumnsCommand;
import core.command.dbscan.DbScanFunctionalCommand;
import core.model.CompiledRegression;
import core.model.Data;
import core.service.ICsvService;
import core.service.IDataService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collection;

@Component
public class DbscanTask extends ExecutionTask {

    private DbScanArguments dbScanArguments;

    public DbscanTask(ICommandExecutor commandExecutor, IDataService dataService, ICsvService csvService,
                      DbScanArguments dbScanArguments) {
        super(commandExecutor, dataService, csvService, dbScanArguments);
        this.dbScanArguments = dbScanArguments;
    }

    @Override
    protected void afterInsertionRun() {
        if (dbScanArguments.isFunctionCheck()) {
            ArangoCursor<Data> datas = executor.execute(new DbScanFunctionalCommand(
                    dataService,
                    dbScanArguments.getRadius(),
                    dbScanArguments.getOutputRadius()
            ));
            ByteArrayOutputStream execute = executor.execute(new DataToCsvCommand(
                    csvService,
                    datas.asListRemaining()
            ));
            save(execute, "dbscan-fc.csv");
        }

        dbScanArguments.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    Collection<CompiledRegression> compiledRegressions = executor.execute(new DbScanAnalyseColumnsCommand(
                            dataService,
                            dbScanArguments.getRadius(),
                            columNo
                    ));
                    ByteArrayOutputStream execute = executor.execute(new CompiledRegressionToCsvCommand(
                            csvService,
                            compiledRegressions
                    ));
                    save(execute, String.format("dbscan-analyse-(%d).csv", columNo));
                });
    }
}
