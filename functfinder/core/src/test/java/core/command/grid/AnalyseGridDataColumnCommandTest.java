package core.command.grid;


import core.Faker;
import core.command.CommandException;
import core.command.CommandTest;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class AnalyseGridDataColumnCommandTest extends CommandTest {

    private AnalyseGridDataColumnCommand sut;

    @Override
    public void before() {
        super.before();
        sut = new AnalyseGridDataColumnCommand(new DataService(operations), new CsvService());
    }

    @Test
    public void shouldWork() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress,
                new AnalyseGridDataColumnCommand.Param(1,
                        Arrays.asList(2.0)));

        assertThat(result.size(), is(greaterThan(0)));
    }


    @Test
    public void WhenIndexMinus1Columns() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress,
                new AnalyseGridDataColumnCommand.Param(-1,
                        Arrays.asList(2.0)));

        assertThat(result.size(), is(greaterThan(0)));
    }


    @Test(expected = CommandException.class)
    public void throwWhenIndexMoreThanColumns() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress,
                new AnalyseGridDataColumnCommand.Param(6,
                        Arrays.asList(2.0)));
    }

    @Test(expected = CommandException.class)
    public void throwWhenIndexLessThanColumns() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress, new AnalyseGridDataColumnCommand.Param(-5,
                Arrays.asList(2.0)));
    }

    @Test(expected = CommandException.class)
    public void throwWhenIndex0Columns() {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);

        ByteArrayOutputStream result = sut.execute(progress, new AnalyseGridDataColumnCommand.Param(0,
                Arrays.asList(2.0)));
    }
}