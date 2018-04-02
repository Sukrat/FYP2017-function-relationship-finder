package cmdapp.tasks;


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

    private DbScanArguments args;

    public DbscanTask(ICommandExecutor commandExecutor, IDataService dataService, ICsvService csvService,
                      DbScanArguments args) {
        super(commandExecutor, dataService, csvService, args);
        this.args = args;
    }

    @Override
    protected void afterInsertionRun() {
        System.out.printf("Dbscan analyse started with radius: %f, cols: %s, %s!\n",
                args.getRadius(), args.getAnalyseColumns().toString(), args.isNormalise() ? "with normalization" : ""
        );
        if (args.isFunctionCheck()) {
            ArangoCursor<Data> datas = executor.execute(new DbScanFunctionalCommand(
                    dataService,
                    args.getRadius(),
                    args.getOutputRadius()
            ));
            ByteArrayOutputStream execute = executor.execute(new DataToCsvCommand(
                    csvService,
                    datas.asListRemaining()
            ));
            save(execute, "dbscan-fc.csv");
        }

        args.getAnalyseColumns()
                .stream()
                .forEach(columNo -> {
                    Collection<CompiledRegression> compiledRegressions = executor.execute(new DbScanAnalyseColumnsCommand(
                            dataService,
                            args.getRadius(),
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
