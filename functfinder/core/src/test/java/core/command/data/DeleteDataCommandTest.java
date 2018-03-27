package core.command.data;

import core.Faker;
import core.command.CommandTest;
import core.service.DataService;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DeleteDataCommandTest extends CommandTest {

    private DeleteDataCommand sut;

    @Override
    public void before() {
        super.before();
        sut = new DeleteDataCommand(new DataService(operations));
    }

    @Test
    public void shouldReturnValues() {
        operations.insert(Faker.nextData("testcsv.csv", 14, 3),
                collectionName);
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                collectionName);
        operations.insert(Faker.nextData("asfdsf.csv", 10, 3),
                collectionName);

        Long result = sut.execute(progress, "testcsv.csv");

        assertThat(result, is(14L));
    }

    @Test
    public void whenEmptyShouldReturnEmptyCollection() {
        Long result = sut.execute(progress, "testcsv.csv");

        assertThat(result, is(0L));
    }
}