package functlyser;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

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
                .user(dbConfig.getUsername())
                .password(dbConfig.getPassword())
                .build();
        return arangoDB;
    }
}
