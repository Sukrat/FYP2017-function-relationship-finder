package core.command.dbscan;

import com.arangodb.ArangoCursor;
import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.model.Data;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DbScanFunctionalCommandTest extends ICommandTest {

    private DbScanFunctionalCommand sut;

    @Test
    public void execute() {
        double nRadius = 2.0;
        double oRadius = 5.0;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new DbScanFunctionalCommand(dataService, nRadius, oRadius);

        ArangoCursor<Data> result = execute(sut);

        assertThat(result.asListRemaining().size(), is(0));
    }


    @Test
    public void execute_shouldWorkWhereItIsNotAFunction() {
        double nRadius = 2.0;
        double oRadius = 1.0;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new DbScanFunctionalCommand(dataService, nRadius, oRadius);

        ArangoCursor<Data> result = execute(sut);

        assertThat(result.asListRemaining().size(), is(greaterThan(0)));
    }

    @Test(expected = CommandException.class)
    public void noDataShouldThrow() {
        double nRadius = 2.0;
        double oRadius = 1.0;
        sut = new DbScanFunctionalCommand(dataService, nRadius, oRadius);

        ArangoCursor<Data> result = execute(sut);
    }

}