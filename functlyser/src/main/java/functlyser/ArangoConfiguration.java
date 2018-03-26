package functlyser;

import com.arangodb.ArangoDB;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArangoConfiguration extends AbstractArangoConfiguration {

    private ArangoDbSettings arangoDbSettings;

    @Autowired
    public ArangoConfiguration(ArangoDbSettings arangoDbSettings) {
        this.arangoDbSettings = arangoDbSettings;
    }

    @Override
    public ArangoDB.Builder arango() {
        return new ArangoDB.Builder()
                .user(arangoDbSettings.getUser())
                .password(arangoDbSettings.getPassword())
                .host(arangoDbSettings.getHost(), arangoDbSettings.getPort())
                .maxConnections(arangoDbSettings.getMaxConnections());
    }

    @Override
    public String database() {
        return arangoDbSettings.getDatabaseName();
    }
}
