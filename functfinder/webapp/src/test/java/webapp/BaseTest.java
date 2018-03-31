package webapp;


import core.arango.Operations;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class BaseTest {

    @Autowired
    protected Operations operations;

    protected String profile = "mvcTest";

    protected String collectionName = profile + "-Data";

    @Before
    public void before() {
        operations.collection(collectionName).truncate();
    }
}
