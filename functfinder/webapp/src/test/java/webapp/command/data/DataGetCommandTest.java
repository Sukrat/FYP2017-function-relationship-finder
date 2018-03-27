package webapp.command.data;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import webapp.BaseTest;
import webapp.Faker;
import webapp.command.CommandException;
import webapp.command.CommandProgess;
import webapp.model.Data;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

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