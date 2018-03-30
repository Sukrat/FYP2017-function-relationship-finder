package cmdapp;

import cmdapp.argument.DatabaseArguments;
import com.arangodb.ArangoDB;
import core.AbstractCoreArangoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"core", "cmdapp"})
public class CoreConfiguration extends AbstractCoreArangoConfiguration {

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
}
