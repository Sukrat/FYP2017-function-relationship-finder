package core.command.data;

import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;
import core.service.IDataService;

public class DataDeleteByFileNameCommand implements ICommand<Long> {

    private IDataService dataService;
    private String fileName;

    public DataDeleteByFileNameCommand(IDataService dataService, String fileName) {
        this.dataService = dataService;
        this.fileName = fileName;
    }

    @Override
    public Long execute(IProgress progress) {
        Data anyByFileName = dataService.findAnyByFileName(fileName);
        if (anyByFileName == null) {
            throw new CommandException("No data found with filename '%s'", fileName);
        }
        progress.setWork(1, "removing data from %s", fileName);

        Long count = dataService.removeByFileName(fileName);

        progress.increment();
        return count;
    }
}
