package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;

public class DataByFileNameCommand implements ICommand<ArangoCursor<Data>> {

    private DataService dataService;
    private String fileName;

    public DataByFileNameCommand(DataService dataService, String fileName) {
        this.dataService = dataService;
        this.fileName = fileName;
    }

    @Override
    public ArangoCursor<Data> execute(IProgress progress) {
        Data anyByFileName = dataService.findAnyByFileName(fileName);
        if (anyByFileName == null) {
            throw new CommandException("Data with filename: '%s' not found!", fileName);
        }
        return dataService.findAllByFileName(fileName);
    }
}
