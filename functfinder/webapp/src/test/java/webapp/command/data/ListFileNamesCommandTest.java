package webapp.command.data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.BaseTest;
import webapp.Faker;
import webapp.model.Data;

import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListFileNamesCommandTest extends BaseTest {

    @Autowired
    private ListFileNamesCommand sut;

    @Test
    public void shouldReturnValues(){
        operations.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);
        operations.insert(Faker.nextData("hello.csv", 10, 3),
                Data.class);
        operations.insert(Faker.nextData("asfdsf.csv", 10, 3),
                Data.class);

        Collection<String> result = sut.execute(null);

        assertThat(result.size(), is(3));
        assertThat(result, containsInAnyOrder("testcsv.csv", "hello.csv", "asfdsf.csv"));
    }

    @Test
    public void whenEmptyShouldReturnEmptyCollection(){
        Collection<String> result = sut.execute(null);

        assertThat(result.size(), is(0));
    }
}