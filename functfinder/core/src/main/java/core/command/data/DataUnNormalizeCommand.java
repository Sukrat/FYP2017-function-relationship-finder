package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;
import core.service.IDataService;

import java.util.HashMap;

public class DataUnNormalizeCommand implements ICommand<Long> {

    private IDataService dataService;

    public DataUnNormalizeCommand(IDataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Long execute(IProgress progress) {
        Data sample = dataService.findAny();
        if (sample == null) {
            throw new CommandException("No data found! Nothing to un-normalize!");
        }

        progress.setWork(1, "Un-normalising values!");

        String query = dataService.join(
                "FOR r IN @@col",
                "UPDATE { _key: r._key, workColumns: r.rawColumns }",
                "IN @@col",
                "COLLECT WITH COUNT INTO c",
                "RETURN c");

        ArangoCursor<Long> result = dataService.query(query, new HashMap<>(), Long.class);

        progress.increment();
        
        return result.asListRemaining().get(0);
    }
}
