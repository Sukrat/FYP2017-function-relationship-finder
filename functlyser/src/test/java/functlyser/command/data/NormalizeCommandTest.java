package functlyser.command.data;

import functlyser.BaseTest;
import functlyser.Faker;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

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