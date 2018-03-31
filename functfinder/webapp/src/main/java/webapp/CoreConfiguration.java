package webapp;

import com.arangodb.ArangoDB;
import core.AbstractCoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"core"})
public class CoreConfiguration extends AbstractCoreConfiguration {

    private AppSettings settings;

    @Autowired
    public CoreConfiguration(AppSettings settings) {
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
