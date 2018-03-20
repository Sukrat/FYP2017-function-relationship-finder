package functlyser.command.data;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import functlyser.command.Command;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import functlyser.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class UnNormalizeCommand implements Command<Void, Long> {

    private ArangoOperations operations;
    private DataRepository dataRepository;

    @Autowired
    public UnNormalizeCommand(ArangoOperations operations, DataRepository dataRepository) {
        this.operations = operations;
        this.dataRepository = dataRepository;
    }

    @Override
    public Long execute(CommandProgess progress, Void aVoid) {
        Data sample = dataRepository.findFirstByRawColumnsNotNull();
        if (sample == null) {
            throw new CommandException("Data not found!");
        }
        int colSize = sample.getRawColumns().size();

        progress.setTotalWork(1, "Updating data with csv values!");

        String query = "FOR r IN @@col\n" +
                "UPDATE { _key: r._key, \n" +
                "workColumns: r.rawColumns\n" +
                "} IN @@col\n" +
                "COLLECT WITH COUNT INTO c\n" +
                "RETURN c\n";

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", Data.class);
        ArangoCursor<Long> result = operations.query(query, bindVar, null, Long.class);
        return result.asListRemaining().get(0);
    }
}
