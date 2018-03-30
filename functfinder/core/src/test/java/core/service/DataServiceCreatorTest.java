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

        assertThat(dataService.collectionName(), is("testData"));
        assertThat(operations.collectionExists("testData"), is(true));
    }

    @Test
    public void create_withAllowedPrefix() {
        IDataService dataService = sut.create("test-64");

        assertThat(dataService.collectionName(), is("test-64Data"));
        assertThat(operations.collectionExists("test-64Data"), is(true));
    }

    @Test(expected = ServiceException.class)
    public void create_withNotAllowedPrefixThrow() {
        IDataService dataService = sut.create("test-64/");
    }
}