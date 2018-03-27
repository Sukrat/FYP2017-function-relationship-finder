package core.command.data;

import core.Faker;
import core.command.CommandTest;
import core.service.DataService;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class ListFileNamesCommandTest extends CommandTest {

    private ListFileNamesCommand sut;

    @Override
    public void before() {
        super.before();
        sut = new ListFileNamesCommand(new DataService(operations));
    }

    @Test
    public void shouldReturnValues() {
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                collectionName);
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                collectionName);
        operations.insert(Faker.nextData("asfdsf.csv", 10, 3),
                collectionName);

        Collection<String> result = sut.execute(progress, null);

        assertThat(result.size(), is(3));
        assertThat(result, containsInAnyOrder("testcsv.csv", "hello.csv", "asfdsf.csv"));
    }

    @Test
    public void whenEmptyShouldReturnEmptyCollection() {
        Collection<String> result = sut.execute(progress, null);

        assertThat(result.size(), is(0));
    }
}