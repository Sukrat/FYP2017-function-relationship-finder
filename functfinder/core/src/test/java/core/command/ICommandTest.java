package core.command;

import core.DbTest;
import core.service.DataService;
import core.service.DataServiceCreator;
import core.service.ICsvService;
import core.service.IDataService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ICommandTest extends DbTest {

    protected IProgress progress;
    protected String collectionName = "cmdTest-Data";

    protected IDataService dataService;

    @Autowired
    protected ICsvService csvService;

    @Autowired
    protected DataServiceCreator dataServiceCreator;

    @Override
    public void before() {
        super.before();
        progress = Mockito.mock(IProgress.class);
        dataService = dataServiceCreator.create("cmdTest");
    }

    public <T> T execute(ICommand<T> command) {
        return command.execute(progress);
    }
}
