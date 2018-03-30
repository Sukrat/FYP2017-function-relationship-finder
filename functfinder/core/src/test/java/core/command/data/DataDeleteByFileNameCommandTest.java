package core.command.data;

import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.model.Data;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataDeleteByFileNameCommandTest extends ICommandTest{

    public DataDeleteByFileNameCommand sut;

    @Test
    public void execute() {
        String fileName = "testcsv.csv";
        operations.insert(Faker.nextData(fileName, 15, 3),
                collectionName);
        sut = new DataDeleteByFileNameCommand(dataService, fileName);

        Long execute = execute(sut);

        assertThat(execute, is(15L));
    }

    @Test(expected = CommandException.class)
    public void execute_whenNotFoundThrowError() {
        String fileName = "testcsv.csv";
        sut = new DataDeleteByFileNameCommand(dataService, fileName);

        Long execute = execute(sut);
    }
}