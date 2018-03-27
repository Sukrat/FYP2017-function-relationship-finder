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

public class NormalizeCommandTest extends BaseTest {

    @Autowired
    private NormalizeCommand sut;

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

        Long result = sut.execute(progess, null);

        assertThat(result, is(10L));
    }

    @Test(expected = CommandException.class)
    public void shouldThrowIfNoDataPresent() {
        Long result = sut.execute(progess, null);
    }
}