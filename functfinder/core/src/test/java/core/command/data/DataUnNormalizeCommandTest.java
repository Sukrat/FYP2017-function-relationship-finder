package core.command.data;

import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DataUnNormalizeCommandTest extends ICommandTest {

    public DataUnNormalizeCommand sut;

    @Test
    public void execute() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                collectionName);
        sut = new DataUnNormalizeCommand(dataService);

        Long result = execute(sut);

        assertThat(result, is(10L));
    }

    @Test(expected = CommandException.class)
    public void execute_whenNotFoundThrowError() {
        sut = new DataUnNormalizeCommand(dataService);

        Long result = execute(sut);
    }
}