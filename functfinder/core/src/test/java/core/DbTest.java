package core;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;

public abstract class DbTest {

    public static String dbName = "test";
    public static String propertiesFileName = "test.properties";

    protected static ArangoDB arangoDB;
    protected ArangoDatabase database;

    @BeforeClass
    public static void beforeClass() throws Exception {
        InputStream input = null;
        try {
            input = DbTest.class.getClassLoader().getResourceAsStream(propertiesFileName);
            if (input == null) {
                throw new Exception("Could not load properties file for testing.");
            }
            arangoDB = new ArangoDB.Builder()
                    .loadProperties(input)
                    .build();
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
