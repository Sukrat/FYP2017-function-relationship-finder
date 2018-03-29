package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.Command;
import core.command.CommandProgess;
import core.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ListFileNamesCommand implements Command<Void, Collection<String>> {

    private DataService dataService;

    @Autowired
    public ListFileNamesCommand(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Collection<String> execute(CommandProgess progess, Void aVoid) {
        ArangoCursor<String> result = dataService.findAllFileNames();
        return result.asListRemaining();
    }
}
