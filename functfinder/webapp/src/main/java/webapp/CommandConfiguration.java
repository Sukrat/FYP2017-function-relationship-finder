package webapp;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import core.arango.ArangoOperation;
import core.arango.Operations;
import core.command.data.*;
import core.command.dbscan.DbScanAnalyseColumnCommand;
import core.command.dbscan.DbScanFunctionalCheckCommand;
import core.command.grid.AnalyseGridDataColumnCommand;
import core.command.grid.GridFunctionCheckCommand;
import core.service.CsvService;
import core.service.DataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {

    @Bean
    public ArangoDB arangoDB(ArangoDbSettings settings) {
        return new ArangoDB.Builder()
                .user(settings.getUser())
                .password(settings.getPassword())
                .host(settings.getHost(), settings.getPort())
                .maxConnections(settings.getMaxConnections())
                .build();
    }

    @Bean
    public ArangoDatabase arangoDatabase(ArangoDB arangoDB, ArangoDbSettings settings) {
        String databaseName = settings.getDatabaseName();
        ArangoDatabase database = arangoDB.db(databaseName);
        try {
            database.getInfo();
        } catch (final ArangoDBException e) {
            if (new Integer(404).equals(e.getResponseCode())) {
                try {
                    arangoDB.createDatabase(databaseName);
                } catch (final ArangoDBException e1) {
                    throw e1;
                }
            } else {
                throw e;
            }
        }
        return database;
    }

    @Bean
    public Operations operations(ArangoDatabase database) {
        return new ArangoOperation(database);
    }

    // services
    @Bean
    public DataService dataService(Operations operations) {
        return new DataService(operations);
    }

    @Bean
    public CsvService csvService() {
        return new CsvService();
    }

    // data commands

    @Bean
    public DataGetCommand dataGetCommand(DataService dataService, CsvService csvService) {
        return new DataGetCommand(csvService, dataService);
    }

    @Bean
    public DataUploadCommand dataUploadCommand(DataService dataService, CsvService csvService) {
        return new DataUploadCommand(csvService, dataService);
    }

    @Bean
    public DeleteDataCommand deleteDataCommand(DataService dataService) {
        return new DeleteDataCommand(dataService);
    }

    @Bean
    public ListFileNamesCommand listFileNamesCommand(DataService dataService) {
        return new ListFileNamesCommand(dataService);
    }

    @Bean
    public NormalizeCommand normalizeCommand(DataService dataService) {
        return new NormalizeCommand(dataService);
    }

    @Bean
    public UnNormalizeCommand unNormalizeCommand(DataService dataService) {
        return new UnNormalizeCommand(dataService);
    }

    // grid commands

    @Bean
    public AnalyseGridDataColumnCommand analyseGridDataColumnCommand(DataService dataService, CsvService csvService) {
        return new AnalyseGridDataColumnCommand(dataService, csvService);
    }

    @Bean
    public GridFunctionCheckCommand gridFunctionCheckCommand(DataService dataService, CsvService csvService) {
        return new GridFunctionCheckCommand(dataService, csvService);
    }

    // dbscan command

    @Bean
    public DbScanAnalyseColumnCommand dbScanAnalyseColumnCommand(DataService dataService, CsvService csvService) {
        return new DbScanAnalyseColumnCommand(dataService, csvService);
    }


    @Bean
    public DbScanFunctionalCheckCommand dbScanFunctionalCheckCommand(DataService dataService, CsvService csvService) {
        return new DbScanFunctionalCheckCommand(dataService, csvService);
    }
}
