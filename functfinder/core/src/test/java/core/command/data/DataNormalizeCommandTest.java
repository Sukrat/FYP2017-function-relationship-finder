package core.command.data;

import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.service.DataService;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataNormalizeCommandTest extends ICommandTest {

    public DataNormalizeCommand sut;

    @Test
    public void execute() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                collectionName);
        sut = new DataNormalizeCommand(dataService);

        Long result = execute(sut);

        assertThat(result, is(10L));
    }

    @Test(expected = CommandException.class)
    public void execute_whenNotFoundThrowError() {
        sut = new DataNormalizeCommand(dataService);

        Long result = execute(sut);
    }
}