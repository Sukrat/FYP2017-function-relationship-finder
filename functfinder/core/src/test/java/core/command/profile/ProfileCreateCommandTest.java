package core.command.profile;

import core.Util;
import core.command.CommandException;
import core.command.ICommandTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProfileCreateCommandTest extends ICommandTest {

    private ProfileCreateCommand sut;

    @Test
    public void execute() {
        String profile = "test-data";
        operations.collection("test-data");
        sut = new ProfileCreateCommand(operations, profile);

        execute(sut);

        assertTrue(operations.collectionExists(Util.dataCollectionName(profile)));
    }

    @Test(expected = CommandException.class)
    public void execute_whenExistThrow() {
        String profile = "test";
        operations.collection("test-Data");
        sut = new ProfileCreateCommand(operations, profile);

        execute(sut);
    }

    @Test(expected = CommandException.class)
    public void execute_whenEmptyThrow() {
        String profile = "";
        sut = new ProfileCreateCommand(operations, profile);

        execute(sut);
    }
}