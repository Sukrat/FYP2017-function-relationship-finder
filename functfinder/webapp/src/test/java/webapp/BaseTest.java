package webapp;


import com.arangodb.springframework.core.ArangoOperations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.BaseTest;
import webapp.Faker;
import webapp.command.CommandException;
import webapp.command.CommandProgess;
import webapp.model.Data;
import webapp.model.GridData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

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
