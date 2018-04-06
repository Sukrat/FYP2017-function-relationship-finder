package core.command.data;

import core.command.ICommand;
import core.command.IProgress;
import core.service.DataService;
import core.service.IDataService;

import java.util.Collection;

public class DataGetFileNamesCommand implements ICommand<Collection<String>> {

    private IDataService dataService;

    public DataGetFileNamesCommand(IDataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Collection<String> execute(IProgress progress) {
        return dataService.findAllFileNames().asListRemaining();
    }
}
