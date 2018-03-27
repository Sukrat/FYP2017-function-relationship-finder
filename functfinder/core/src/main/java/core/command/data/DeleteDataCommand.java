package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.Command;
import core.command.CommandProgess;
import core.model.Data;
import core.service.DataService;

import java.util.HashMap;
import java.util.Map;

public class DeleteDataCommand implements Command<String, Long> {

    private DataService dataService;

    public DeleteDataCommand(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Long execute(CommandProgess progress, String fileName) {
        progress.update(0, 1, "Removing data from '%s'", fileName);
        Long count = dataService.removeByFileName(fileName);
        progress.update(1, 1, "%d data removed from '%s'", count, fileName);
        return count;
    }
}
