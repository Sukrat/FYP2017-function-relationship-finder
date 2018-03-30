package webapp;

import com.arangodb.ArangoDB;
import core.AbstractCoreArangoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"core"})
public class CoreArangoConfiguration extends AbstractCoreArangoConfiguration {

    private AppSettings settings;

    @Autowired
    public CoreArangoConfiguration(AppSettings settings) {
        this.settings = settings;
    }

    @Override
    public String dbName() {
        return settings.getDbname();
    }

    @Override
    public ArangoDB.Builder arangoDBBuilder() {
        return new ArangoDB.Builder();
    }
}
