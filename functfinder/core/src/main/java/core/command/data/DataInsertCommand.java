package core.command.data;

import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;

import java.util.Collection;

public class DataInsertCommand implements ICommand<Long> {

    private DataService dataService;
    private Collection<Data> datas;

    public DataInsertCommand(DataService dataService, Collection<Data> datas) {
        this.dataService = dataService;
        this.datas = datas;
    }

    @Override
    public Long execute(IProgress progress) {
        Long count = 0L;
        if (datas == null || datas.isEmpty()) {
            return count;
        }
        Data any = datas.stream().findAny().get();
        if (any.getWorkColumns().size() < 2) {
            throw new CommandException("Number of columns must be greater than 2!");
        }

        Data sample = dataService.findAny();
        if (sample != null) {
            if (any.getWorkColumns().size() != sample.getWorkColumns().size()) {
                throw new CommandException("Number of columns donot match! (expected: %d, actual: %d)",
                        sample.getWorkColumns().size(), any.getWorkColumns().size());
            }
        }

        Collection<Data> insert = dataService.insert(datas);
        return insert.stream().count();
    }
}
