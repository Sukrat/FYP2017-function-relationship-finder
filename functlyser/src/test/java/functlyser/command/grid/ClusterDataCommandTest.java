package functlyser.command.grid;

import functlyser.BaseTest;
import functlyser.Faker;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.command.data.DataUploadCommand;
import functlyser.model.Data;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ClusterDataCommandTest extends BaseTest {

    @Autowired
    private ClusterDataCommand sut;

    private CommandProgess progess;


    @Override
    public void before() {
        super.before();
        progess = Mockito.mock(CommandProgess.class);
    }

    @Test
    public void shouldWork() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);

        Long result = sut.execute(progess, Arrays.asList(2.0,2.0));

        assertThat(result, is(5L));
    }

    @Test
    public void shouldWorkWithOneTolerance() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 4),
                Data.class);

        Long result = sut.execute(progess, Arrays.asList(2.0));

        assertThat(result, is(5L));
    }

    @Test(expected = CommandException.class)
    public void shouldThrowIfNoDataPresent() {
        Long result = sut.execute(progess, null);
    }

    @Test(expected = CommandException.class)
    public void shouldThrowIfNoToleranceIsNull() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);

        Long result = sut.execute(progess, null);
    }

    @Test(expected = CommandException.class)
    public void shouldThrowIfNoToleranceIsLess() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 4),
                Data.class);

        Long result = sut.execute(progess, Arrays.asList(2.0,2.0));
    }

}