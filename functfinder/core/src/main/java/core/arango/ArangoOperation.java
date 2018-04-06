package core.arango;

import com.arangodb.*;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.IndexEntity;
import com.arangodb.entity.MultiDocumentEntity;
import com.arangodb.model.HashIndexOptions;
import com.arangodb.model.SkiplistIndexOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

public class ArangoOperation implements Operations {

    private ArangoDatabase database;

    public ArangoOperation(ArangoDatabase arangoDatabase) {
        this.database = arangoDatabase;
    }

    @Override
    public ArangoCollection collection(String collectionName) {
        throwIfNullOrEmpty(collectionName);

        ArangoCollection collection = database.collection(collectionName);
        if (!collection.exists()) {
            database.createCollection(collectionName);
        }
        return collection;
    }

    @Override
    public Set<String> getCollectionNames() {
        return database.getCollections().stream()
                .filter(m -> !m.getIsSystem())
                .map(m -> m.getName())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean collectionExists(String collectionName) {
        throwIfNullOrEmpty(collectionName);

        return database.collection(collectionName).exists();
    }

    @Override
    public void dropCollection(String collectionName) {
        throwIfNullOrEmpty(collectionName);

        ArangoCollection collection = database.collection(collectionName);
        if (collection.exists()) {
            collection.drop();
        }
    }

    @Override
    public <T extends Entity> T findAny(String collectionName, Class<T> entity) {
        throwIfNullOrEmpty(collectionName);

        collection(collectionName);
        String query = "FOR c IN @@col LIMIT 1 RETURN c";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", collectionName);
        ArangoCursor<T> documents = database.query(query, bindVar, null, entity);
        T result = null;
        for (T document : documents) {
            result = document;
            break;
        }
        return result;
    }

    @Override
    public <T> ArangoCursor<T> findAll(String collectionName, Class<T> entity) {
        throwIfNullOrEmpty(collectionName);

        collection(collectionName);
        String query = "FOR c IN @@col RETURN c";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", collectionName);
        return database.query(query, bindVar, null, entity);
    }

    @Override
    public long count(String collectionName) {
        throwIfNullOrEmpty(collectionName);

        long count = 0;
        ArangoCollection collection = database.collection(collectionName);
        if (collection.exists()) {
            count = collection.count().getCount();
        }
        return count;
    }

    @Override
    public <T extends Entity> T insert(T objectToSave, String collectionName) {
        throwIfNullOrEmpty(collectionName);

        ArangoCollection collection = collection(collectionName);
        DocumentCreateEntity<T> result = collection.insertDocument(objectToSave);
        updateFields(objectToSave, result);
        return objectToSave;
    }

    @Override
    public <T extends Entity> Collection<T> insert(Collection<T> batchToSave, String collectionName) {
        throwIfNullOrEmpty(collectionName);

        ArangoCollection collection = collection(collectionName);
        MultiDocumentEntity<DocumentCreateEntity<T>> result = collection.insertDocuments(batchToSave);

        if (result.getErrors().size() > 0) {
            List<String> errors = result.getErrors()
                    .stream()
                    .map(m -> String.format("Error code: %d, exception: %s, message: %s",
                            m.getCode(), m.getException(), m.getErrorMessage()))
                    .collect(Collectors.toList());
            throw new RuntimeException(String.join("\n", errors));
        }

        Iterator<T> batchIterator = batchToSave.iterator();
        Iterator<DocumentCreateEntity<T>> docIterator = result.getDocuments().iterator();
        for (; batchIterator.hasNext() && docIterator.hasNext(); ) {
            T batch = batchIterator.next();
            DocumentCreateEntity<T> document = docIterator.next();
            updateFields(batch, document);
        }
        return batchToSave;
    }

    @Override
    public <T> ArangoCursor<T> query(
            final String query,
            final Map<String, Object> bindVars,
            final Class<T> type) throws ArangoDBException {
        return database.query(query, bindVars, null, type);
    }

    @Override
    public IndexEntity ensureSkipListIndex(String collectionName, Collection<String> fields, final SkiplistIndexOptions options) {
        throwIfNullOrEmpty(collectionName);

        ArangoCollection collection = collection(collectionName);
        return collection.ensureSkiplistIndex(fields, options);
    }

    @Override
    public IndexEntity ensureHashIndex(String collectionName, Collection<String> fields, final HashIndexOptions options) {
        throwIfNullOrEmpty(collectionName);

        ArangoCollection collection = collection(collectionName);
        return collection.ensureHashIndex(fields, options);
    }

    @Override
    public <T extends Entity> String name(Class<T> type) {
        return type.getSimpleName();
    }

    private <T extends Entity> void updateFields(T object, DocumentEntity d) {
        object.setId(d.getId());
        object.setKey(d.getKey());
        object.setRev(d.getRev());
    }

    private void throwIfNullOrEmpty(String collectionName) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null!");
        if (collectionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be empty!");
        }
    }
}