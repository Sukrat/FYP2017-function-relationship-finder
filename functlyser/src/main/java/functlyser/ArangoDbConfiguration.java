package functlyser;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArangoDbConfiguration {

    @Bean
    public ArangoDatabase arangoDatabase(ArangoDB connection, ArangoDbConfig dbConfig) {
        ArangoDatabase database = connection.db(dbConfig.getDbname());
        return database;
    }

    @Bean
    public ArangoDB arangoDB(ArangoDbConfig dbConfig) {
        ArangoDB arangoDB = new ArangoDB.Builder()
                .maxConnections(dbConfig.getNoOfConnections())
                .user(dbConfig.getUsername())
                .password(dbConfig.getPassword())
                .build();
        return arangoDB;
    }
}
