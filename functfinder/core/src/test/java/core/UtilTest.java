package core;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


public class UtilTest {

    @Test
    public void dataCollectionName() {
        String result = Util.dataCollectionName("test");

        assertThat(result, is("test-Data"));
    }

    @Test
    public void dataCollectionName_withEmptyProfile() {
        String result = Util.dataCollectionName("  ");

        assertThat(result, is("Data"));
    }

    @Test
    public void getProfile() {
        String result = Util.getProfile("test-data-Data");

        assertThat(result, is("test-data"));
    }

    @Test
    public void getProfile_whenEmpty() {
        String result = Util.getProfile("Data");

        assertThat(result, is(""));
    }

    @Test
    public void getCollection() {
        String result = Util.getCollection("test-data-Data");

        assertThat(result, is("Data"));
    }

    @Test
    public void getCollection_whenEmpty() {
        String result = Util.getCollection("Data");

        assertThat(result, is("Data"));
    }
}