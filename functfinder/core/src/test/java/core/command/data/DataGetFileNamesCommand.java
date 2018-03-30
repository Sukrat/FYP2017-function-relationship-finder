package core.command.data;

import core.command.ICommand;
import core.command.IProgress;
import core.service.DataService;

import java.util.Collection;

public class DataGetFileNamesCommand implements ICommand<Collection<String>> {

    private DataService dataService;

    public DataGetFileNamesCommand(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Collection<String> execute(IProgress progress) {
        return dataService.findAllFileNames().asListRemaining();
    }
}
