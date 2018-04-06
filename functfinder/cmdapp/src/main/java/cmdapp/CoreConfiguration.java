package cmdapp;

import cmdapp.argument.DatabaseArguments;
import com.arangodb.ArangoDB;
import core.AbstractCoreConfiguration;
import core.service.DataServiceCreator;
import core.service.IDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"core", "cmdapp"})
public class CoreConfiguration extends AbstractCoreConfiguration {

    private DatabaseArguments args;

    public CoreConfiguration(DatabaseArguments args) {
        this.args = args;
    }

    @Override
    public String dbName() {
        return args.getDatabaseName();
    }

    @Override
    public ArangoDB.Builder arangoDBBuilder() {
        return new ArangoDB.Builder()
                .user(args.getUser())
                .password(args.getPassword())
                .host(args.getHost(), args.getPort())
                .maxConnections(args.getMaxConnections());
    }

    @Bean
    public IDataService dataService(DataServiceCreator creator) {
        return creator.create(args.getProfileName());
    }
}
