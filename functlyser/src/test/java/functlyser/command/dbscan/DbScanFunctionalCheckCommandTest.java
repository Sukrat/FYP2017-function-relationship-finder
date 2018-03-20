package functlyser.command.dbscan;

import functlyser.BaseTest;
import functlyser.Faker;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.command.grid.AnalyseGridDataColumnCommand;
import functlyser.command.grid.ClusterDataCommand;
import functlyser.model.Data;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DbScanFunctionalCheckCommandTest extends BaseTest {

    @Autowired
    private DbScanFunctionalCheckCommand sut;

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

        Resource result = sut.execute(progress, new DbScanFunctionalCheckCommand.Param(2.0, 5.0));

        assertThat(result.contentLength(), is(0l));
    }

    @Test
    public void shouldWork_WhereItIsNotAFunction() throws IOException {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, Data.class);

        Resource result = sut.execute(progress, new DbScanFunctionalCheckCommand.Param(2.0, 1.0));

        Assert.assertThat(result.contentLength(), greaterThan(0L));
    }

    @Test(expected = CommandException.class)
    public void noDataShouldThrow() {
        Resource result = sut.execute(progress, new DbScanFunctionalCheckCommand.Param(2.0, 1.0));
    }


}