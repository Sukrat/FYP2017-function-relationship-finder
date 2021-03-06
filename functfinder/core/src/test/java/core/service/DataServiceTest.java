package core.service;

import core.DbTest;
import core.Faker;
import core.model.Data;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataServiceTest extends DbTest {

    private IDataService sut;
    public static String collectionName = "Data";

    @Override
    public void before() {
        super.before();
        sut = new DataService(operations, collectionName);
    }

    @Test
    public void findAny() {
        operations.insert(Faker.nextData("t", 2, 3), collectionName);

        Data result = sut.findAny();

        assertThat(result, is(notNullValue()));
        assertThat(result.getFileName(), is("t"));
        assertThat(result.getRawColumns().size(), is(3));
        assertThat(result.getWorkColumns().size(), is(3));
    }

    @Test
    public void findAny_whenEmptyShouldReturnNull() {
        Data result = sut.findAny();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void findAnyByFileName() {
        String filename = "t";
        operations.insert(Faker.nextData(filename, 2, 3), collectionName);
        operations.insert(Faker.nextData("t5", 2, 3), collectionName);

        Data result = sut.findAnyByFileName(filename);

        assertThat(result, is(notNullValue()));
        assertThat(result.getFileName(), is(filename));
        assertThat(result.getRawColumns().size(), is(3));
        assertThat(result.getWorkColumns().size(), is(3));
    }

    @Test
    public void findAnyByFileName_whenEmptyShouldReturnNull() {
        String filename = "t";

        Data result = sut.findAnyByFileName(filename);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void findAllByFileName() {
        String filename = "t";
        operations.insert(Faker.nextData(filename, 2, 3), collectionName);
        operations.insert(Faker.nextData("t5", 2, 3), collectionName);

        List<Data> result = sut.findAllByFileName(filename)
                .asListRemaining();

        assertThat(result.size(), is(2));
    }

    @Test
    public void findAllByFileName_whenEmptyShouldReturnNull() {
        String filename = "t";

        List<Data> result = sut.findAllByFileName(filename)
                .asListRemaining();

        assertThat(result.size(), is(0));
    }

    @Test
    public void insert_single() {
        Data data = Faker.nextData("t5", 1, 10).get(0);

        Data result = sut.insert(data);

        assertThat(result.getId(), not(emptyOrNullString()));
    }

    @Test
    public void insert() {
        List<Data> datas = Faker.nextData("t5", 10, 10);

        Collection<Data> result = sut.insert(datas);


        assertThat(result.size(), is(10));
        result.stream().forEach(m -> {
            assertThat(m.getId(), not(emptyOrNullString()));
        });
        assertThat(database.collection(collectionName).count().getCount(), is(10L));
    }

    @Test
    public void removeByFileName() {
        String filename = "t";
        operations.insert(Faker.nextData(filename, 2, 10), collectionName);
        operations.insert(Faker.nextData("t5", 5, 13), collectionName);

        long result = sut.removeByFileName(filename);

        assertThat(result, is(2L));
        assertThat(database.collection(collectionName).count().getCount(), is(5L));
    }

    @Test
    public void removeByFileName_whenFileNameDoesnotExistShouldReturn0() {
        String filename = "t";
        operations.insert(Faker.nextData("t5", 2, 13), collectionName);

        long result = sut.removeByFileName(filename);

        assertThat(result, is(0L));
        assertThat(database.collection(collectionName).count().getCount(), is(2L));
    }

    @Test
    public void findAllFileNames() {
        operations.insert(Faker.nextData("t1", 2, 10), collectionName);
        operations.insert(Faker.nextData("t2", 2, 13), collectionName);

        List<String> result = sut.findAllFileNames()
                .asListRemaining();

        assertThat(result.size(), is(2));
        assertThat(result, containsInAnyOrder("t1", "t2"));
    }

    @Test
    public void collectionName() {
        String result = sut.collectionName();

        assertThat(result, is(collectionName));
    }

    @Test
    public void join() {
        String result = sut.join("abc", "def", "fgh");

        assertThat(result, is("abc\ndef\nfgh\n"));
    }

    @Test
    public void findAllIds() {
        operations.insert(Faker.nextData("t1", 2, 10), collectionName);
        operations.insert(Faker.nextData("t2", 2, 13), collectionName);

        List<Data> result = sut.findAllIds()
                .asListRemaining();

        result.stream()
                .forEach(m -> {
                    assertThat(m.getId(), not(emptyOrNullString()));
                    assertThat(m.getRawColumns(), is(nullValue()));
                    assertThat(m.getWorkColumns(), is(nullValue()));
                });
    }

}