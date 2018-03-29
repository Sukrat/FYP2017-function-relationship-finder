package core;

import com.arangodb.ArangoDB;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"core"})
public class CoreArangoTestConfiguration extends AbstractCoreArangoConfiguration {

    @Override
    public String dbName() {
        return "test";
    }

    @Override
    public ArangoDB.Builder arangoDBBuilder() {
        return new ArangoDB.Builder();
    }
}
