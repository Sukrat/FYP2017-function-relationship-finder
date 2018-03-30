package core.command;

import core.DbTest;
import core.service.DataService;
import core.service.IDataService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ICommandTest extends DbTest {

    protected IProgress progress;
    protected String collectionName = "Data";

    protected IDataService dataService;

    @Override
    public void before() {
        super.before();
        progress = Mockito.mock(IProgress.class);
        dataService = new DataService(operations);
    }

    public <T> T execute(ICommand<T> command) {
        return command.execute(progress);
    }
}
