package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.Command;
import core.command.CommandProgess;
import core.model.Data;
import core.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UnNormalizeCommand implements Command<Void, Long> {

    private DataService dataService;


    @Autowired
    public UnNormalizeCommand(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Long execute(CommandProgess progress, Void aVoid) {
        Data sample = dataService.findAny();
        if (sample == null) {
            progress.update("No data found! Nothing to normalize!");
            return 0L;
        }
        int colSize = sample.getRawColumns().size();

        progress.update(0, 1, "Updating data with csv values!");
        String query = dataService.join(
                "FOR r IN @@col",
                "UPDATE { _key: r._key, workColumns: r.rawColumns }",
                "IN @@col",
                "COLLECT WITH COUNT INTO c",
                "RETURN c");

        ArangoCursor<Long> result = dataService.query(query, new HashMap<>(), Long.class);
        progress.update(1, 1, "Un-normalising values finished!");
        return result.asListRemaining().get(0);
    }
}
