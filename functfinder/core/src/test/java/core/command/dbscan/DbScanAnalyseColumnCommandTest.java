package core.command.dbscan;

import core.Faker;
import core.command.CommandException;
import core.command.CommandProgess;
import core.command.CommandTest;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class DbScanAnalyseColumnCommandTest extends CommandTest {

    private DbScanAnalyseColumnCommand sut;

    @Override
    public void before() {
        super.before();
        sut = new DbScanAnalyseColumnCommand(new DataService(operations), new CsvService());
    }

    @Test
    public void shouldWork() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress, new DbScanAnalyseColumnCommand.Param(2.0, 1));

        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test(expected = CommandException.class)
    public void throwWhenIndex0Columns() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress, new DbScanAnalyseColumnCommand.Param(2.0, 0));
    }

    @Test
    public void shouldWork_WhenIndexMinus1Columns() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress, new DbScanAnalyseColumnCommand.Param(2.0, -1));

        assertThat(result.size(), is(greaterThan(0)));
    }
}