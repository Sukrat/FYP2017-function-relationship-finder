package core.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.IndexEntity;
import com.arangodb.model.HashIndexOptions;
import com.arangodb.model.SkiplistIndexOptions;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Operations {

    ArangoCollection collection(String collectionName);

    Set<String> getCollectionNames();

    boolean collectionExists(String collectionName);

    void dropCollection(String collectionName);

    <T extends Entity> T findAny(String collectionName, Class<T> entity);

    <T> ArangoCursor<T> findAll(String collectionName, Class<T> entity);

    long count(String collectionName);

    <T extends Entity> T insert(T objectToSave, String collectionName);

    <T extends Entity> Collection<T> insert(Collection<T> batchToSave, String collectionName);

    <T> ArangoCursor<T> query(
            String query,
            Map<String, Object> bindVars,
            Class<T> type) throws ArangoDBException;

    IndexEntity ensureSkipListIndex(String collectionName, Collection<String> fields, SkiplistIndexOptions options);

    IndexEntity ensureHashIndex(String collectionName, Collection<String> fields, HashIndexOptions options);

    <T extends Entity> String name(Class<T> type);
}
