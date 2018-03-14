package functlyser.repository;


import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ArangoOperationTest {

    @Autowired
    private ArangoDatabase database;

    @Autowired
    private ArangoOperation sut;

    @Before
    public void before() {
        database.getCollections()
                .stream()
                .filter(m -> !m.getIsSystem())
                .forEach(m -> database.collection(m.getName()).drop());
    }

    @After
    public void after() {
        database.getCollections()
                .stream()
                .filter(m -> !m.getIsSystem())
                .forEach(m -> database.collection(m.getName()).drop());
    }

    @Test
    public void collection_WithTypeParam() {
        Class<Test> type = Test.class;

        ArangoCollection result = sut.collection(type);

        assertThat(result, notNullValue());
        assertThat(result.count().getCount(), is(0L));
        assertThat(result.name(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void collection_WithNullTypeParam() {
        Class<Test> type = null;

        ArangoCollection result = sut.collection(type);
    }

    @Test
    public void collection_mustCreateIfNotPresent() {
        String collectionName = "sukrat";

        ArangoCollection result = sut.collection(collectionName);

        assertThat(result, notNullValue());
        assertThat(result.count().getCount(), is(0L));
        assertThat(result.name(), is(collectionName));
        assertTrue(database.collection(collectionName).exists());
    }

//    @Test
//    public void collection_ifAlreadyCreatedShouldGiveCacheValue() {
//        ArangoCollection resultX = sut.collection("sukrat");
//        ArangoCollection resultY = sut.collection("sukrat");
//
//        assertThat(resultX, is(resultY));
//    }


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
    public void collectionExists_WithTypeWhenTrue() {
        Class<Test> type = Test.class;
        database.createCollection("test");

        boolean result = sut.collectionExists(type);

        assertTrue(result);
    }

    @Test
    public void collectionExists_WithTypeWhenFalse() {
        Class<Test> type = Test.class;

        boolean result = sut.collectionExists(type);

        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionExists_WithNullTypeShouldThrow() {
        Class<Test> type = null;

        boolean result = sut.collectionExists(type);
    }

    @Test
    public void collectionExists_IfPresentReturnTrue() {
        String collectionName = "sukrat";
        database.createCollection(collectionName);

        boolean result = sut.collectionExists(collectionName);

        assertTrue(result);
    }

    @Test
    public void collectionExists_IfNotPresentReturnFalse() {
        String collectionName = "sukrat";

        boolean result = sut.collectionExists(collectionName);

        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionExists_IfEmptyShouldThrow() {
        String collectionName = "";

        boolean result = sut.collectionExists(collectionName);
    }

    @Test
    public void dropCollection_WithType() {
        Class<Test> type = Test.class;
        database.createCollection("test");

        sut.dropCollection(type);

        assertFalse(database.getCollections().stream()
                .anyMatch(m -> m.getName().equals("test")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dropCollection_WithNullShouldThrow() {
        Class<Test> type = null;

        sut.dropCollection(type);
    }

    @Test
    public void dropCollection() {
        String collectionName = "sukrat";
        database.createCollection(collectionName);

        sut.dropCollection(collectionName);

        assertFalse(database.getCollections().stream()
                .anyMatch(m -> m.getName().equals(collectionName)));
    }

    @Test
    public void dropCollection_whenCollectionNotPresentStillWork() {
        String collectionName = "sukrat";

        sut.dropCollection(collectionName);

        assertFalse(database.getCollections().stream()
                .anyMatch(m -> m.getName().equals(collectionName)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dropCollection_whenEmptyStringShouldThrow() {
        String collectionName = "";

        sut.dropCollection(collectionName);
    }

    @Test
    public void findAny() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");
        database.collection("testdata").insertDocument(new TestData("sukrat", 35, 6.2));

        TestData all = sut.findAny(TestData.class);

        assertThat(all.getKey(), not(isEmptyOrNullString()));
    }


    @Test
    public void findAll() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");
        database.collection("testdata").insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat25", 35, 6.2));

        ArangoCursor<TestData> all = sut.findAll(TestData.class);

        assertThat(all.asListRemaining().size(), is(3));
    }

    @Test
    public void findAll_WhenEmpty() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");

        ArangoCursor<TestData> all = sut.findAll(TestData.class);

        assertThat(all.asListRemaining().size(), is(0));
    }

    @Test
    public void findAll_WhenNoCollection() {
        Class<TestData> type = TestData.class;

        ArangoCursor<TestData> all = sut.findAll(TestData.class);

        assertThat(all.asListRemaining().size(), is(0));
    }

    @Test
    public void findAll_WhenString() {
        String collectionName = "testdata";
        database.createCollection(collectionName);
        database.collection(collectionName).insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat25", 35, 6.2));

        ArangoCursor<TestData> all = sut.findAll(collectionName, TestData.class);

        assertThat(all.asListRemaining().size(), is(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAll_WhenEmptyString() {
        ArangoCursor<TestData> all = sut.findAll("", TestData.class);
    }

    @Test
    public void count() {
        String collectionName = "testdata";
        database.createCollection(collectionName);
        database.collection(collectionName).insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat25", 35, 6.2));

        long result = sut.count(collectionName);

        assertThat(result, is(3L));
    }

    @Test
    public void count_IfDocumentDonotExist() {
        String collectionName = "testdata";

        long result = sut.count(collectionName);

        assertThat(result, is(0L));
    }

    @Test
    public void insert() {
        String collectionName = "testdata";
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData);

        assertThat(result.getKey(), not(isEmptyOrNullString()));
    }

    @Test
    public void insert_WithCollectionName() {
        String collectionName = "testdatas";
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData, collectionName);

        assertThat(result.getKey(), not(isEmptyOrNullString()));
    }

    @Test
    public void insert_Bulk() {
        String collectionName = "testdata";
        TestData testData = new TestData("sukrat", 35, 6.2);
        TestData testData2 = new TestData("sukrat2", 35, 6.2);
        TestData testData3 = new TestData("sukrat3", 35, 6.2);
        Collection<TestData> collection = Arrays.asList(testData, testData2, testData3);

        Collection<TestData> result = sut.insert(collection, TestData.class);

        assertThat(result.size(), is(3));
        assertTrue(result.stream().allMatch(m -> not(isEmptyOrNullString()).matches(m.getKey())));
    }
}
