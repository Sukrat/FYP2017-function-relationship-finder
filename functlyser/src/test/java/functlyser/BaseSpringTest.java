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
    private ArangoDatabase database;

    @Autowired
    protected ArangoOperation arangoOperation;

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


}
