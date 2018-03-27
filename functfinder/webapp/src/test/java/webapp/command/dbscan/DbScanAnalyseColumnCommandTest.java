package webapp.command.dbscan;

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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class DbScanAnalyseColumnCommandTest extends BaseTest {

    @Autowired
    private DbScanAnalyseColumnCommand sut;

    private CommandProgess progress;


    @Override
    public void before() {
        super.before();
        progress = Mockito.mock(CommandProgess.class);
    }

    @Test
    public void shouldWork() throws IOException {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, Data.class);

        Resource result = sut.execute(progress, new DbScanAnalyseColumnCommand.Param(2.0, 1));

        assertThat(result.contentLength(), is(greaterThan(0l)));
    }

    @Test(expected = CommandException.class)
    public void throwWhenIndex0Columns() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, Data.class);

        Resource result = sut.execute(progress, new DbScanAnalyseColumnCommand.Param(2.0, 0));
    }

    @Test
    public void shouldWork_WhenIndexMinus1Columns() throws IOException {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, Data.class);

        Resource result = sut.execute(progress, new DbScanAnalyseColumnCommand.Param(2.0, -1));

        assertThat(result.contentLength(), is(greaterThan(0l)));
    }
}