package functlyser;

import com.arangodb.ArangoDatabase;
import functlyser.repository.ArangoOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public abstract class BaseSpringTest {

    @Autowired
    protected MongoOperations mongoOperations;

    @Autowired
    private ArangoDatabase database;

    @Autowired
    protected ArangoOperation arangoOperation;

    @Before
    public void before() {
        Set<String> collectionNames = mongoOperations.getCollectionNames();
        for (String collectionName :
                collectionNames) {
            if (mongoOperations.collectionExists(collectionName)) {
                mongoOperations.dropCollection(collectionName);
            }
            mongoOperations.createCollection(collectionName);
        }
        database.getCollections()
                .stream()
                .filter(m -> !m.getIsSystem())
                .forEach(m -> database.collection(m.getName()).drop());
    }

    @After
    public void after() {
        Set<String> collectionNames = mongoOperations.getCollectionNames();
        for (String collectionName :
                collectionNames) {
            mongoOperations.dropCollection(collectionName);
        }
        database.getCollections()
                .stream()
                .filter(m -> !m.getIsSystem())
                .forEach(m -> database.collection(m.getName()).drop());
    }


}
