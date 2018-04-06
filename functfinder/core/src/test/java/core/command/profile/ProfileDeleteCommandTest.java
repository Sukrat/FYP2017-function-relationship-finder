package core.command.profile;

import core.Util;
import core.command.CommandException;
import core.command.ICommandTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ProfileDeleteCommandTest extends ICommandTest {

    private ProfileDeleteCommand sut;

    @Test
    public void execute() {
        String profile = "test-data";
        operations.collection(Util.dataCollectionName(profile));
        sut = new ProfileDeleteCommand(operations, profile);

        execute(sut);

        assertThat(operations.collectionExists(Util.dataCollectionName(profile)), is(false));
    }

    @Test(expected = CommandException.class)
    public void execute_whenEmptyProfileThrow() {
        String profile = "  ";
        sut = new ProfileDeleteCommand(operations, profile);

        execute(sut);
    }

    @Test(expected = CommandException.class)
    public void execute_whenNotPresentProfileThrow() {
        String profile = "test-data";
        sut = new ProfileDeleteCommand(operations, profile);

        execute(sut);
    }
}