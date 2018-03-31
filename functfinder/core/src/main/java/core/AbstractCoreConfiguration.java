package core;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import core.arango.ArangoOperation;
import core.arango.Operations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class AbstractCoreConfiguration {

    @Bean
    public abstract String dbName();

    @Bean
    public abstract ArangoDB.Builder arangoDBBuilder();

    @Bean
    public ArangoDB arangoDB() {
        return arangoDBBuilder().build();
    }

    @Bean
    public ArangoDatabase arangoDatabase(ArangoDB arangoDB) {
        String databaseName = dbName();
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
    public Operations operations(ArangoDatabase arangoDatabase) {
        return new ArangoOperation(arangoDatabase);
    }
}