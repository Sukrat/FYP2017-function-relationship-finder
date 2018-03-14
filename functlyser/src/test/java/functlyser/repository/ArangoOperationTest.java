package functlyser.repository;


import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.IndexEntity;
import org.hamcrest.core.IsNull;
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
import static org.hibernate.validator.internal.util.StringHelper.isNullOrEmptyString;
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
    public void collection_withTypeParam() {
        Class<TestData> type = TestData.class;

        ArangoCollection result = sut.collection(type);

        assertThat(result, notNullValue());
        assertThat(result.count().getCount(), is(0L));
        assertThat(result.name(), is("testdata"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void collection_withNullTypeParam() {
        Class<TestData> type = null;

        ArangoCollection result = sut.collection(type);
    }

    @Test
    public void collection_withNameParam() {
        String collectionName = "sukrat";

        ArangoCollection result = sut.collection(collectionName);

        assertThat(result, notNullValue());
        assertThat(result.count().getCount(), is(0L));
        assertThat(result.name(), is(collectionName));
        assertTrue(database.collection(collectionName).exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void collection_withEmptyCollectionName() {

        ArangoCollection result = sut.collection(" ");
    }

    @Test
    public void getCollectionNames() {
        database.createCollection("t1");
        database.createCollection("t2");

        Set<String> result = sut.getCollectionNames();

        assertThat(result.size(), is(2));
        assertThat(result, contains("t1", "t2"));
    }

    @Test
    public void collectionExists_withTypeWhenTrue() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");

        boolean result = sut.collectionExists(type);

        assertTrue(result);
    }

    @Test
    public void collectionExists_withTypeWhenFalse() {
        Class<TestData> type = TestData.class;

        boolean result = sut.collectionExists(type);

        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionExists_withNullTypeShouldThrow() {
        Class<TestData> type = null;

        boolean result = sut.collectionExists(type);
    }

    @Test
    public void collectionExists_withNameIfPresentReturnTrue() {
        String collectionName = "sukrat";
        database.createCollection(collectionName);

        boolean result = sut.collectionExists(collectionName);

        assertTrue(result);
    }

    @Test
    public void collectionExists_withNameIfNotPresentReturnFalse() {
        String collectionName = "sukrat";

        boolean result = sut.collectionExists(collectionName);

        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionExists_ifEmptyShouldThrow() {
        String collectionName = "";

        boolean result = sut.collectionExists(collectionName);
    }

    @Test
    public void dropCollection_withType() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");

        sut.dropCollection(type);

        assertFalse(database.getCollections().stream()
                .anyMatch(m -> m.getName().equals("test")));
    }

    @Test
    public void dropCollection_shouldWorkWhenCollectionDoesntExist() {
        Class<TestData> type = TestData.class;

        sut.dropCollection(type);

        assertFalse(database.getCollections().stream()
                .anyMatch(m -> m.getName().equals("test")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dropCollection_WithNullShouldThrow() {
        Class<TestData> type = null;

        sut.dropCollection(type);
    }

    @Test
    public void dropCollection_withName() {
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
    public void findAny_mustReturnElement() {
        TestData data = new TestData("sukrat", 35, 6.2);
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");
        database.collection("testdata")
                .insertDocument(data);

        TestData all = sut.findAny(TestData.class);

        assertThat(all.getKey(), not(isEmptyOrNullString()));
        assertThat(all.getName(), is(data.getName()));
        assertThat(all.getAge(), is(data.getAge()));
        assertThat(all.getMoney(), is(data.getMoney()));
        assertThat(all.getCreatedOn(), is(data.getCreatedOn()));
    }

    @Test
    public void findAny_mustReturnNullIfNotPresent() {
        TestData data = new TestData("sukrat", 35, 6.2);

        TestData all = sut.findAny(TestData.class);

        assertThat(all, is(nullValue()));
    }


    @Test
    public void findAll() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");
        database.collection("testdata").insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat25", 35, 6.2));

        ArangoCursor<TestData> all = sut.findAll(TestData.class);

        List<TestData> result = all.asListRemaining();
        assertThat(result.size(), is(3));
        assertFalse(result.stream()
                .anyMatch(m -> isNullOrEmptyString(m.getKey())));
        assertFalse(result.stream()
                .anyMatch(m -> isNullOrEmptyString(m.getId())));
    }

    @Test
    public void findAll_whenEmpty() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");

        ArangoCursor<TestData> all = sut.findAll(TestData.class);

        assertThat(all.asListRemaining().size(), is(0));
    }

    @Test
    public void findAll_whenNoCollection() {
        Class<TestData> type = TestData.class;

        ArangoCursor<TestData> all = sut.findAll(TestData.class);

        assertThat(all.asListRemaining().size(), is(0));
    }

    @Test
    public void findAll_whenString() {
        String collectionName = "testdata";
        database.createCollection(collectionName);
        database.collection(collectionName).insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat25", 35, 6.2));

        ArangoCursor<TestData> all = sut.findAll(collectionName, TestData.class);

        List<TestData> result = all.asListRemaining();
        assertThat(result.size(), is(3));
        assertFalse(result.stream()
                .anyMatch(m -> isNullOrEmptyString(m.getKey())));
        assertFalse(result.stream()
                .anyMatch(m -> isNullOrEmptyString(m.getId())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAll_whenEmptyString() {

        ArangoCursor<TestData> all = sut.findAll("", TestData.class);
    }

    @Test
    public void count_withTypeParam() {
        String collectionName = "testdata";
        database.createCollection(collectionName);
        database.collection(collectionName).insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat25", 35, 6.2));

        long result = sut.count(TestData.class);

        assertThat(result, is(3L));
    }

    @Test
    public void count_withName() {
        String collectionName = "testdata";
        database.createCollection(collectionName);
        database.collection(collectionName).insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection(collectionName).insertDocument(new TestData("sukrat25", 35, 6.2));

        long result = sut.count(collectionName);

        assertThat(result, is(3L));
    }

    @Test
    public void count_ifDocumentDonotExist() {
        String collectionName = "testdata";

        long result = sut.count(collectionName);

        assertThat(result, is(0L));
    }

    @Test
    public void insert_withTypeParam_whenCollectionDoesntExist() {
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData);

        assertThat(result.getKey(), not(isEmptyOrNullString()));
    }

    @Test
    public void insert_withTypeParam_whenCollectionDoesExist() {
        database.createCollection("testdata");
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData);

        assertThat(result.getKey(), not(isEmptyOrNullString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_withNullTypeParam_shouldThrow() {
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(null);
    }

    @Test
    public void insert_withCollectionName() {
        String collectionName = "testdatas";
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData, collectionName);

        assertThat(result.getKey(), not(isEmptyOrNullString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_withEmptyCollectionName_shouldThrow() {
        String collectionName = "testdatas";
        TestData testData = new TestData("sukrat", 35, 6.2);

        TestData result = sut.insert(testData, "");
    }

    @Test
    public void insert_Bulk_withTypeParam() {
        TestData testData = new TestData("sukrat", 35, 6.2);
        TestData testData2 = new TestData("sukrat2", 35, 6.2);
        TestData testData3 = new TestData("sukrat3", 35, 6.2);
        Collection<TestData> collection = Arrays.asList(testData, testData2, testData3);

        Collection<TestData> result = sut.insert(collection, TestData.class);

        assertThat(result.size(), is(3));
        assertTrue(result.stream().allMatch(m -> not(isEmptyOrNullString()).matches(m.getKey())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_Bulk_withNullData() {
        Collection<TestData> result = sut.insert(null, TestData.class);

        assertThat(result.size(), is(3));
        assertTrue(result.stream().allMatch(m -> not(isEmptyOrNullString()).matches(m.getKey())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_Bulk_withNullTypeParam() {
        Class type = null;
        TestData testData = new TestData("sukrat", 35, 6.2);
        TestData testData2 = new TestData("sukrat2", 35, 6.2);
        TestData testData3 = new TestData("sukrat3", 35, 6.2);
        Collection<TestData> collection = Arrays.asList(testData, testData2, testData3);

        Collection<TestData> result = sut.insert(collection, type);
    }

    @Test
    public void insert_Bulk_withNameParam() {
        TestData testData = new TestData("sukrat", 35, 6.2);
        TestData testData2 = new TestData("sukrat2", 35, 6.2);
        TestData testData3 = new TestData("sukrat3", 35, 6.2);
        Collection<TestData> collection = Arrays.asList(testData, testData2, testData3);

        Collection<TestData> result = sut.insert(collection, "testdata");

        assertThat(result.size(), is(3));
        assertTrue(result.stream().allMatch(m -> not(isEmptyOrNullString()).matches(m.getKey())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_Bulk_withEmptyNameParam() {
        TestData testData = new TestData("sukrat", 35, 6.2);
        TestData testData2 = new TestData("sukrat2", 35, 6.2);
        TestData testData3 = new TestData("sukrat3", 35, 6.2);
        Collection<TestData> collection = Arrays.asList(testData, testData2, testData3);

        Collection<TestData> result = sut.insert(collection, "");

        assertThat(result.size(), is(3));
        assertTrue(result.stream().allMatch(m -> not(isEmptyOrNullString()).matches(m.getKey())));
    }

    @Test
    public void ensureSkipListIndex_withTypeParam() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");
        database.collection("testdata").insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat25", 35, 6.2));

        IndexEntity result = sut.ensureSkipListIndex(TestData.class, Arrays.asList("name", "age"));

        assertTrue(result.getDeduplicate());
        assertFalse(result.getUnique());
        assertFalse(result.getSparse());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureSkipListIndex_withNullTypeParam() {
        Class<TestData> type = null;
        IndexEntity result = sut.ensureSkipListIndex(type, Arrays.asList("name", "age"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureSkipListIndex_withEmptyNameParam() {
        IndexEntity result = sut.ensureSkipListIndex("", Arrays.asList("name", "age"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureSkipListIndex_withEmptyFieldsParam() {
        IndexEntity result = sut.ensureSkipListIndex("testdata", Arrays.asList());
    }

    @Test
    public void ensureSkipListIndexMulti_withTypeParam() {
        Class<TestData> type = TestData.class;
        database.createCollection("testdata");
        database.collection("testdata").insertDocument(new TestData("sukrat", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat2", 35, 6.2));
        database.collection("testdata").insertDocument(new TestData("sukrat25", 35, 6.2));

        List<IndexEntity> result = sut.ensureSkipListIndexMulti(TestData.class, Arrays.asList("name", "age"));

        assertTrue(result.stream().allMatch(m -> m.getDeduplicate()));
        assertTrue(result.stream().allMatch(m -> !m.getUnique()));
        assertTrue(result.stream().allMatch(m -> !m.getSparse()));
    }
}
