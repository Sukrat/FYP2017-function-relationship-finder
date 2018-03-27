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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class DeleteDataCommandTest extends BaseTest {

    @Autowired
    private DeleteDataCommand sut;

    private CommandProgess progess;

    @Override
    public void before() {
        super.before();
        progess = Mockito.mock(CommandProgess.class);
    }

    @Test
    public void shouldReturnValues() {
        operations.insert(Faker.nextData("testcsv.csv", 14, 3),
                Data.class);
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                Data.class);
        operations.insert(Faker.nextData("asfdsf.csv", 10, 3),
                Data.class);

        Long result = sut.execute(progess, "testcsv.csv");

        assertThat(result, is(14L));
    }

    @Test
    public void whenEmptyShouldReturnEmptyCollection() {
        Long result = sut.execute(progess, "testcsv.csv");

        assertThat(result, is(0L));
    }
}