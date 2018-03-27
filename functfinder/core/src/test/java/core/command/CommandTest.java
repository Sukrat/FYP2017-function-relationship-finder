package core.command;

import core.DbTest;
import core.arango.ArangoOperation;
import core.arango.Operations;
import core.command.CommandProgess;
import org.mockito.Mockito;

public abstract class CommandTest extends DbTest {

    protected CommandProgess progress;
    protected Operations operations;
    protected String collectionName = "Data";

    @Override
    public void before() {
        super.before();
        progress = Mockito.mock(CommandProgess.class);
        operations = new ArangoOperation(database);
    }
}
