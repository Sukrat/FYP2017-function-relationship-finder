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


public class DbScanFunctionalCheckCommandTest extends CommandTest {

    private DbScanFunctionalCheckCommand sut;

    @Override
    public void before() {
        super.before();
        sut = new DbScanFunctionalCheckCommand(new DataService(operations), new CsvService());
    }

    @Test
    public void shouldWork() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress, new DbScanFunctionalCheckCommand.Param(2.0, 5.0));

        assertThat(result.size(), is(0));
    }

    @Test
    public void shouldWork_WhereItIsNotAFunction() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress, new DbScanFunctionalCheckCommand.Param(2.0, 1.0));

        assertThat(result.size(), greaterThan(0));
    }

    @Test(expected = CommandException.class)
    public void noDataShouldThrow() {
        ByteArrayOutputStream result = sut.execute(progress, new DbScanFunctionalCheckCommand.Param(2.0, 1.0));
    }


}