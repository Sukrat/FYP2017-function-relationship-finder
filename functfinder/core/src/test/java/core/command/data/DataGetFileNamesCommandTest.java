package core.command.data;

import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataGetFileNamesCommandTest extends ICommandTest {

    public DataGetFileNamesCommand sut;

    @Test
    public void execute() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                collectionName);
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                collectionName);
        operations.insert(Faker.nextData("asfdsf.csv", 10, 3),
                collectionName);
        sut = new DataGetFileNamesCommand(dataService);

        Collection<String> result = execute(sut);

        assertThat(result.size(), is(3));
        assertThat(result, containsInAnyOrder("testcsv.csv", "hello.csv", "asfdsf.csv"));
    }

    @Test
    public void execute_whenNotFoundThrowError() {
        sut = new DataGetFileNamesCommand(dataService);

        Collection<String> result = execute(sut);

        assertThat(result.size(), is(0));
    }
}