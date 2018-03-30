package core.service;

import com.arangodb.ArangoCursor;
import core.arango.Operations;
import core.model.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Component
public class DataService {

    private Operations operations;

    @Autowired
    public DataService(Operations operations) {
        this.operations = operations;
        operations.collection(collectionName());
    }

    public Data findAny() {
        return operations.findAny(collectionName(), Data.class);
    }

    public Data findAnyByFileName(String fileName) {
        String query = join(
                "FOR r in @@col",
                "FILTER r.fileName == @filename",
                "LIMIT 1",
                "RETURN r");
        ArangoCursor<Data> queryResult = operations.query(query, new HashMap<String, Object>() {{
            put("@col", collectionName());
            put("filename", fileName);
        }}, Data.class);

        Data data = null;
        List<Data> datas = queryResult.asListRemaining();
        if (!datas.isEmpty()) {
            data = datas.get(0);
        }
        return data;
    }

    public ArangoCursor<Data> findAllByFileName(String fileName) {
        String query = join(
                "FOR r in @@col",
                "FILTER r.fileName == @filename",
                "RETURN r");
        ArangoCursor<Data> queryResult = operations.query(query, new HashMap<String, Object>() {{
            put("@col", collectionName());
            put("filename", fileName);
        }}, Data.class);
        return queryResult;
    }

    public Collection<Data> insert(Collection<Data> datas) {
        return operations.insert(datas, collectionName());
    }

    public long removeByFileName(String fileName) {
        String query = join(
                "FOR r IN @@col",
                "FILTER r.fileName == @filename",
                "REMOVE r IN @@col",
                "COLLECT WITH COUNT INTO c",
                "RETURN c");
        ArangoCursor<Long> result = operations.query(query, new HashMap<String, Object>() {{
            put("@col", collectionName());
            put("filename", fileName);
        }}, Long.class);
        return result.asListRemaining().get(0);
    }

    public ArangoCursor<String> findAllFileNames() {
        String query = join(
                "FOR r IN @@col",
                "RETURN DISTINCT r.fileName");
        ArangoCursor<String> result = operations.query(query, new HashMap<String, Object>() {{
            put("@col", collectionName());
        }}, String.class);
        return result;
    }

    public <T> ArangoCursor<T> query(String query, Map<String, Object> bindVars, Class<T> entity) {
        ArangoCursor<T> result = operations.query(query, new HashMap<String, Object>(bindVars) {{
            put("@col", collectionName());
        }}, entity);
        return result;
    }

    public void ensureSkipListIndex(Collection<String> fields) {
        operations.ensureSkipListIndex(collectionName(), fields, null);
    }

    public String collectionName() {

        return Data.class.getSimpleName();
    }

    public String join(String... s) {
        return String.join("\n", s) + "\n";
    }
}
