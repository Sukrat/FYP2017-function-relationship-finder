package core.command.data;

import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;
import core.service.IDataService;

import java.util.Collection;

public class DataInsertCommand implements ICommand<Long> {

    private IDataService dataService;
    private Collection<Data> datas;

    public DataInsertCommand(IDataService dataService, Collection<Data> datas) {
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
                throw new CommandException("Number of columns donot match in file '%s'! (expected: %d, actual: %d)",
                        any.getFileName(),
                        sample.getWorkColumns().size(), any.getWorkColumns().size());
            }
        }
        progress.setWork(datas.size(), "Inserting data form file: '%s'", any.getFileName());

        count = datas.parallelStream()
                .map(d -> {
                    progress.increment();
                    return dataService.insert(d);
                }).count();
        return count;
    }
}
