package functlyser;


import com.arangodb.springframework.core.ArangoOperations;
import functlyser.model.Data;
import functlyser.model.GridData;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public abstract class BaseTest {

    @Autowired
    protected ArangoOperations operations;

    @Before
    public void before() {
        operations.collection(Data.class).truncate();
        operations.collection(GridData.class).truncate();
    }

    @After
    public void after() {


    }

}
