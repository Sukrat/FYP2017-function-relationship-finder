package functlyser.service;

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
public abstract class BaseServiceTest {

    @Autowired
    protected MongoOperations mongoOperations;

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
    }

    @After
    public void after() {
        Set<String> collectionNames = mongoOperations.getCollectionNames();
        for (String collectionName :
                collectionNames) {
            mongoOperations.dropCollection(collectionName);
        }
    }

}
