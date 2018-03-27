package core.command.data;

import core.Faker;
import core.command.CommandTest;
import core.service.CsvService;
import core.service.DataService;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataGetCommandTest extends CommandTest {

    private DataGetCommand sut;

    @Override
    public void before() {
        super.before();
        sut = new DataGetCommand(new CsvService(), new DataService(operations));
    }

    @Test
    public void whenDataIsThere() throws IOException {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                collectionName);
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                collectionName);

        ByteArrayOutputStream result = sut.execute(progress, "hello.csv");

        assertThat(result.size(), greaterThan(0));
    }

    @Test
    public void getCsvFile_whenFileIsNotThere() throws IOException {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                collectionName);

        ByteArrayOutputStream result = sut.execute(progress, "hello.csv");

        assertThat(result.size(), is(0));
    }
}