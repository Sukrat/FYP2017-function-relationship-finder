package functlyser.command.data;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import functlyser.command.Command;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import functlyser.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeleteDataCommand implements Command<String, Long> {

    private ArangoOperations operations;

    @Autowired
    public DeleteDataCommand(ArangoOperations operations) {
        this.operations = operations;
    }

    @Override
    public Long execute(CommandProgess progress, String s) {
        progress.setTotalWork(1, "Removing data from '%s'", s);

        String query = "FOR r IN @@col\n" +
                "FILTER r.fileName == @fileName\n" +
                "REMOVE r IN @@col\n" +
                "COLLECT WITH COUNT INTO c\n" +
                "RETURN c";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", Data.class);
        bindVar.put("fileName", s);
        ArangoCursor<Long> result = operations.query(query, bindVar, null, Long.class);
        Long count = result.asListRemaining().get(0);

        progress.update(1, "%d data removed from '%s'", count, s);
        return count;
    }
}
