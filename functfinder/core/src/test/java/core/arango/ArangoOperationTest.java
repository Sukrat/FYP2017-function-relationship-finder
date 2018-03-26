package core.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.entity.IndexEntity;
import core.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ArangoOperationTest extends BaseTest {

    private Operations sut;

    @Before
    public void before() {
        super.before();
        sut = new ArangoOperation(database);
    }

    @Test
    public void collection() {
        ArangoCollection result = sut.collection("t");

        assertThat(result, is(notNullValue()));
        assertThat(result.exists(), is(true));
    }

    @Test
    public void collection_mustCreateIfNotPresent() {
        sut.collection("t1");
        sut.collection("t2");
        sut.collection("t3");

        Set<String> result = sut.getCollectionNames();
        assertThat(result, containsInAnyOrder("t1", "t2", "t3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void collection_withEmptyCollectionName() {
        ArangoCollection result = sut.collection("");
    }

    @Test
    public void getCollectionNames() {
        database.createCollection("t1");
        database.createCollection("t2");

        Set<String> result = sut.getCollectionNames();

        assertThat(result, contains("t1", "t2"));
    }

    @Test
    public void collectionExists_IfPresentReturnTrue() {
        String collectionName = "sukrat";
        database.createCollection(collectionName);

        boolean result = sut.collectionExists(collectionName);

        assertThat(result, is(true));
    }

    @Test
    public void collectionExists_IfNotPresentReturnFalse() {
        String collectionName = "sukrat";

        boolean result = sut.collectionExists(collectionName);

        assertThat(result, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionExists_IfEmptyShouldThrow() {
        String collectionName = "";

        boolean result = sut.collectionExists(collectionName);
    }

    @Test
    public void dropCollection() {
        String collectionName = "sukrat";
        database.createCollection(collectionName);

        sut.dropCollection(collectionName);

        assertThat(database.getCollections().stream()
                .anyMatch(m -> m.getName().equals(collectionName)), is(false));
    }

    @Test
    public void dropCollection_whenCollectionNotPresentStillWork() {
        String collectionName = "sukrat";

        sut.dropCollection(collectionName);

        assertThat(database.getCollections().stream()
                .anyMatch(m -> m.getName().equals(collectionName)), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dropCollection_whenEmptyStringShouldThrow() {
        String collectionName = "";

        sut.dropCollection(collectionName);
    }

    @Test
    public void findAny() {
        Class<TestData> type = TestData.class;
        database.createCollection("t");
        database.collection("t").insertDocument(new TestData("sukrat", 35, 6.2));

        TestData all = sut.findAny("t", TestData.class);

        assertThat(all.getKey(), not(emptyOrNullString()));
    }


    @Test
    public void findAll() {
        Class<TestData> type = TestData.class;
        database.createCollection("t1");
        database.collection("t1").insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection("t1").insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection("t1").insertDocument(new TestData("sukrat25", 35, 6.2));

        ArangoCursor<TestData> all = sut.findAll("t1", TestData.class);

        assertThat(all.asListRemaining().size(), is(3));
    }

    @Test
    public void findAll_WhenEmpty() {
        Class<TestData> type = TestData.class;
        database.createCollection("t");

        ArangoCursor<TestData> all = sut.findAll("t", TestData.class);

        assertThat(all.asListRemaining().size(), is(0));
    }

    @Test
    public void findAll_WhenNoCollection() {
        Class<TestData> type = TestData.class;

        ArangoCursor<TestData> all = sut.findAll("t1", TestData.class);

        assertThat(all.asListRemaining().size(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAll_WhenEmptyString() {
        ArangoCursor<TestData> all = sut.findAll("", TestData.class);
    }

    @Test
    public void count() {
        String collectionName = "t";
        database.createCollection(collectionName);
        database.collection(collectionName).insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat25", 35, 6.2));

        long result = sut.count(collectionName);

        assertThat(result, is(3L));
    }

    @Test
    public void count_IfDocumentDonotExist() {
        String collectionName = "t";

        long result = sut.count(collectionName);

        assertThat(result, is(0L));
    }

    @Test
    public void insert() {
        String collectionName = "t";
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData, "t");

        assertThat(result.getKey(), not(emptyOrNullString()));
    }

    @Test
    public void insert_WithCollectionName() {
        String collectionName = "testdatas";
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData, collectionName);

        assertThat(result.getId(), not(emptyOrNullString()));
        assertThat(result.getKey(), not(emptyOrNullString()));
        assertThat(result.getRev(), not(emptyOrNullString()));
    }

    @Test
    public void insert_Bulk() {
        String collectionName = "t";
        TestData testData = new TestData("sukrat", 35, 6.2);
        TestData testData2 = new TestData("sukrat2", 35, 6.2);
        TestData testData3 = new TestData("sukrat3", 35, 6.2);
        Collection<TestData> collection = Arrays.asList(testData, testData2, testData3);

        Collection<TestData> result = sut.insert(collection, "t");

        assertThat(result.size(), is(3));
        result.stream()
                .forEach(m -> {
                    assertThat(m.getId(), not(emptyOrNullString()));
                    assertThat(m.getKey(), not(emptyOrNullString()));
                    assertThat(m.getRev(), not(emptyOrNullString()));
                });
    }

    @Test
    public void ensureSkipListIndex() {
        String collectionName = "t";

        IndexEntity result = sut.ensureSkipListIndex(collectionName,
                Arrays.asList("attr"), null);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), not(emptyOrNullString()));
    }

    @Test
    public void ensureHashIndex() {
        String collectionName = "t";

        IndexEntity result = sut.ensureHashIndex(collectionName,
                Arrays.asList("attr"), null);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), not(emptyOrNullString()));
    }
}
