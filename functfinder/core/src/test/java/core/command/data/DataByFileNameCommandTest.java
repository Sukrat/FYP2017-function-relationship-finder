package core.command.data;

import com.arangodb.ArangoCursor;
import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DataByFileNameCommandTest extends ICommandTest {

    private DataByFileNameCommand sut;


    @Test
    public void execute_whenDataIsPresent() {
        String fileName = "testcsv.csv";
        operations.insert(Faker.nextData("testcsv.csv", 4, 3),
                collectionName);
        operations.insert(Faker.nextData("hello.csv", 13, 3),
                collectionName);
        sut = new DataByFileNameCommand(dataService, fileName);

        ArangoCursor<Data> result = execute(sut);

        assertThat(result.asListRemaining().size(), is(4));
    }

    @Test(expected = CommandException.class)
    public void getCsvFile_whenDataIsNotPresent() {
        String fileName = "testcsv.csv";
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                collectionName);
        sut = new DataByFileNameCommand(dataService, fileName);

        ArangoCursor<Data> result = execute(sut);
    }
}