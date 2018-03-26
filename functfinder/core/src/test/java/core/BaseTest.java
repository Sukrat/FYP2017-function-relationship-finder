package core;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class BaseTest {

    protected ArangoDB arangoDB;

    protected ArangoDatabase database;

    protected static Properties properties = new Properties();

    @BeforeClass
    public static void beforeClass() throws Exception {
        InputStream input = null;
        try {
            String filename = "test.properties";
            input = BaseTest.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                throw new Exception("Could not load properties file for testing.");
            }

            //load a properties file from class path, inside static method
            properties.load(input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Before
    public void before() {
        ArangoDB.Builder builder = new ArangoDB.Builder();
        builder.user(properties.getProperty("user", "root"));
        builder.password(properties.getProperty("password", null));
        arangoDB = builder.build();

        String dbName = properties.getProperty("database", "test");
        database = arangoDB.db(dbName);
        if (database.exists()) {
            database.drop();
        }
        arangoDB.createDatabase(dbName);
    }

    @After
    public void after() {
        database.drop();
    }
}
