package core.service;

import core.DbTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataServiceCreatorTest extends DbTest {

    @Autowired
    private DataServiceCreator sut;

    @Test
    public void create() {
        IDataService dataService = sut.create("test");

        assertThat(dataService.collectionName(), is("test-Data"));
        assertThat(operations.collectionExists("test-Data"), is(true));
    }

    @Test
    public void create_withEmptyPrefix() {
        IDataService dataService = sut.create("  ");

        assertThat(dataService.collectionName(), is("Data"));
        assertThat(operations.collectionExists("Data"), is(true));
    }

    @Test
    public void create_withAllowedPrefix() {
        IDataService dataService = sut.create("test-64");

        assertThat(dataService.collectionName(), is("test-64-Data"));
        assertThat(operations.collectionExists("test-64-Data"), is(true));
    }

    @Test(expected = ServiceException.class)
    public void create_withNotAllowedPrefixThrow() {
        IDataService dataService = sut.create("test-64/");
    }
}