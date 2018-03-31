package core.command.grid;

import com.arangodb.ArangoCursor;
import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.model.Data;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class GridFunctionCommandTest extends ICommandTest {

    private GridFunctionCommand sut;

    @Test
    public void execute() {
        List<Double> nTolerances = Arrays.asList(2.0);
        Double oTolerance = 2.0;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new GridFunctionCommand(dataService,
                nTolerances,
                oTolerance);

        ArangoCursor<Data> result = execute(sut);

        assertThat(result.asListRemaining().size(), is(0));
    }

    @Test
    public void execute_WhereItIsNotAFunction() {
        List<Double> nTolerances = Arrays.asList(2.0);
        Double oTolerance = 1.0;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new GridFunctionCommand(dataService,
                nTolerances,
                oTolerance);

        ArangoCursor<Data> result = execute(sut);

        assertThat(result.asListRemaining().size(), greaterThan(0));
    }

    @Test(expected = CommandException.class)
    public void execute_NoDataShouldThrow() {
        List<Double> nTolerances = Arrays.asList(2.0);
        Double oTolerance = 1.0;
        sut = new GridFunctionCommand(dataService,
                nTolerances,
                oTolerance);

        ArangoCursor<Data> result = execute(sut);
    }
}