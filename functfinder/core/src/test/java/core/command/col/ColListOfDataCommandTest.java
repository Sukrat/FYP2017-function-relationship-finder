package core.command.col;

import core.DbTest;
import core.command.ICommandTest;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ColListOfDataCommandTest extends ICommandTest {

    private ColListOfDataCommand sut;

    @Test
    public void execute() {
        operations.collection("hello");
        operations.collection("hello-Data");
        operations.collection("hello-data");
        sut = new ColListOfDataCommand(operations);

        Collection<String> result = execute(sut);

        assertThat(result.size(), is(1));
        assertThat(result, contains("hello-Data"));
    }

    @Test
    public void execute_whenEmpty() {
        sut = new ColListOfDataCommand(operations);

        Collection<String> result = execute(sut);

        assertThat(result.size(), is(0));
    }
}