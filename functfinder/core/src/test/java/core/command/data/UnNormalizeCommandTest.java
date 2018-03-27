package core.command.data;

import core.Faker;
import core.command.CommandTest;
import core.service.DataService;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class UnNormalizeCommandTest extends CommandTest {

    private UnNormalizeCommand sut;

    @Override
    public void before() {
        super.before();
        sut = new UnNormalizeCommand(new DataService(operations));
    }

    @Test
    public void shouldWork() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                collectionName);

        Long result = sut.execute(progress, null);

        assertThat(result, is(10L));
    }

    @Test
    public void shouldThrowIfNoDataPresent() {
        Long result = sut.execute(progress, null);

        assertThat(result, is(0L));
    }
}