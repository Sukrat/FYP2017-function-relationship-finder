package functlyser.command.data;

import functlyser.BaseTest;
import functlyser.Faker;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DataGetCommandTest extends BaseTest {

    @Autowired
    private DataGetCommand sut;

    private CommandProgess progess;

    private DataUploadCommand.Param param;

    @Override
    public void before() {
        super.before();
        progess = Mockito.mock(CommandProgess.class);
        param = Mockito.mock(DataUploadCommand.Param.class);
    }

    @Test
    public void whenDataIsThere() throws IOException {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                Data.class);

        Resource multi = sut.execute("hello.csv");
        assertThat(multi.contentLength(), greaterThan(0L));
    }

    @Test(expected = CommandException.class)
    public void getCsvFile_whenFileIsNotThere() throws IOException {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);

        Resource multi = sut.execute("hello.csv");
    }
}