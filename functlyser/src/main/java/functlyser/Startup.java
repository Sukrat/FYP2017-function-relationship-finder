package functlyser;

import com.arangodb.springframework.core.ArangoOperations;
import functlyser.model.Data;
import functlyser.model.GridData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Startup {

    private ArangoOperations operations;

    @Autowired
    public Startup(ArangoOperations operations) {
        this.operations = operations;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        operations.collection(Data.class);
        operations.collection(GridData.class);
    }
}
