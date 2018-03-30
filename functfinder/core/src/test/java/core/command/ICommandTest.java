package core.command;

import core.DbTest;
import org.mockito.Mockito;

public class ICommandTest extends DbTest {

    protected IProgress progress;
    protected String profileName = "Data";

    @Override
    public void before() {
        super.before();
        progress = Mockito.mock(IProgress.class);
    }

    public <T> T execute(ICommand<T> command) {
        return command.execute(progress);
    }
}
