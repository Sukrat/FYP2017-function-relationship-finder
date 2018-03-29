package core;

import com.arangodb.ArangoDatabase;
import core.arango.ArangoOperation;
import core.arango.Operations;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CoreArangoTestConfiguration.class)
public abstract class DbTest {

    @Autowired
    protected ArangoDatabase database;

    @Autowired
    protected Operations operations;

    @Before
    public void before() {
        resetDb();
    }

    @After
    public void after() {
        resetDb();
    }

    private void resetDb() {
        database.getCollections()
                .stream()
                .filter(m -> !m.getIsSystem())
                .forEach(m -> database.collection(m.getName()).drop());
    }
}
