package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;

import java.util.HashMap;

public class DataUnNormalizeCommand implements ICommand<Long> {

    private DataService dataService;

    public DataUnNormalizeCommand(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Long execute(IProgress progress) {
        Data sample = dataService.findAny();
        if (sample == null) {
            throw new CommandException("No data found! Nothing to normalize!");
        }

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
