package core.command.profile;

import core.command.ICommandTest;
import core.command.profile.ProfileListCommand;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ProfileListCommandTest extends ICommandTest {

    private ProfileListCommand sut;

    @Test
    public void execute() {
        operations.collection("hello");
        operations.collection("hello-Data");
        operations.collection("hello-data");
        sut = new ProfileListCommand(operations);

        Collection<String> result = execute(sut);

        assertThat(result.size(), is(1));
        assertThat(result, contains("hello"));
    }

    @Test
    public void execute_whenEmpty() {
        sut = new ProfileListCommand(operations);

        Collection<String> result = execute(sut);

        assertThat(result.size(), is(0));
    }
}