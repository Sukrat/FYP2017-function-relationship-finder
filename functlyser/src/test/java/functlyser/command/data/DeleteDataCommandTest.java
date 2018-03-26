package functlyser.command.data;

import functlyser.BaseTest;
import functlyser.Faker;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
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