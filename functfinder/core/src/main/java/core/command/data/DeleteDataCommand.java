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
public class DeleteDataCommand implements Command<String, Long> {

    private DataService dataService;


    @Autowired
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
