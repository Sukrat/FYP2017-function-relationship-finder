package functlyser.command.data;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import functlyser.command.ResultCommand;
import functlyser.model.Data;
import functlyser.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ListFileNamesCommand implements ResultCommand<Void, Collection<String>> {

    private DataRepository dataRepository;
    private ArangoOperations operations;

    @Autowired
    public ListFileNamesCommand(DataRepository dataRepository, ArangoOperations operations) {
        this.dataRepository = dataRepository;
        this.operations = operations;
    }

    @Override
    public Collection<String> execute(Void aVoid) {
        String query = "FOR r IN @@col RETURN DISTINCT r.fileName";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", Data.class);
        ArangoCursor<String> result = operations.query(query, bindVar, null, String.class);
        return result.asListRemaining();
    }
}
