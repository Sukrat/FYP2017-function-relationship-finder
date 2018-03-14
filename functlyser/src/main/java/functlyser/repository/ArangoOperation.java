package functlyser.repository;

import com.arangodb.*;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.MultiDocumentEntity;
import functlyser.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ArangoOperation {

    public static final String ERROR_TYPE_NULL = "Type passed must not be null!";
    public static final String ERROR_COLLECTION_STRING_EMPTY = "Collection name string must not be empty!";

    private ArangoDatabase database;

    @Autowired
    public ArangoOperation(ArangoDatabase arangoDatabase) {
        this.database = arangoDatabase;
    }

    public <T> ArangoCollection collection(Class<T> type) {
        Assert.notNull(type, ERROR_TYPE_NULL);

        return collection(name(type));
    }

    public ArangoCollection collection(String collectionName) {
        Assert.hasText(collectionName, ERROR_COLLECTION_STRING_EMPTY);

        ArangoCollection collection = database.collection(collectionName);
        if (!collection.exists()) {
            database.createCollection(collectionName);
        }
        return collection;
    }

    public <T> String collectionName(Class<T> type) {
        collection(type);
        return name(type);
    }

    public Set<String> getCollectionNames() {
        return database.getCollections().stream()
                .filter(m -> !m.getIsSystem())
                .map(m -> m.getName())
                .collect(Collectors.toSet());
    }

    public <T> boolean collectionExists(Class<T> type) {
        Assert.notNull(type, ERROR_TYPE_NULL);

        return collectionExists(name(type));
    }

    public boolean collectionExists(String collectionName) {
        Assert.hasText(collectionName, ERROR_COLLECTION_STRING_EMPTY);

        return database.collection(collectionName).exists();
    }

    public <T> void dropCollection(Class<T> type) {
        Assert.notNull(type, ERROR_TYPE_NULL);

        dropCollection(name(type));
    }

    public void dropCollection(String collectionName) {
        Assert.hasText(collectionName, ERROR_COLLECTION_STRING_EMPTY);

        ArangoCollection collection = database.collection(collectionName);
        if (collection.exists()) {
            collection.drop();
        }
    }

    public <T> T findAny(Class<T> type) {
        Assert.notNull(type, ERROR_TYPE_NULL);

        collection(name(type));
        String query = "FOR c IN @@collection LIMIT 1 RETURN c";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", name(type));
        ArangoCursor<T> documents = database.query(query, bindVar, null, type);
        T result = null;
        for (T document : documents) {
            result = document;
            break;
        }
        return result;
    }

    public <T> ArangoCursor<T> findAll(Class<T> type) {
        Assert.notNull(type, ERROR_TYPE_NULL);

        return findAll(name(type), type);
    }

    public <T> ArangoCursor<T> findAll(String collectionName, Class<T> entityClass) {
        Assert.hasText(collectionName, ERROR_COLLECTION_STRING_EMPTY);
        Assert.notNull(entityClass, ERROR_TYPE_NULL);

        collection(collectionName);
        String query = "FOR c IN @@collection RETURN c";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", collectionName);
        return database.query(query, bindVar, null, entityClass);
    }

    public long count(Class<?> type) {
        Assert.notNull(type, ERROR_TYPE_NULL);

        return count(type.getSimpleName());
    }

    public long count(String collectionName) {
        Assert.hasText(collectionName, ERROR_COLLECTION_STRING_EMPTY);

        long count = 0;
        ArangoCollection collection = database.collection(collectionName);
        if (collection.exists()) {
            count = collection.count().getCount();
        }
        return count;
    }

    public <T extends Entity> T insert(T objectToSave) {
        return insert(objectToSave, name(objectToSave.getClass()));
    }

    public <T extends Entity> T insert(T objectToSave, String collectionName) {
        Assert.notNull(objectToSave, "Object about to be saved cannot be null!");
        Assert.hasText(collectionName, ERROR_COLLECTION_STRING_EMPTY);

        ArangoCollection collection = collection(collectionName);
        DocumentCreateEntity<T> result = collection.insertDocument(objectToSave);

        updateFields(objectToSave, result);
        return objectToSave;
    }

    public <T extends Entity> Collection<T> insert(Collection<T> batchToSave, Class<T> type) {
        return insert(batchToSave, name(type));
    }


    public <T extends Entity> Collection<T> insert(Collection<T> batchToSave, String collectionName) {
        Assert.notEmpty(batchToSave, "List of object about to be saved cannot be empty!");
        Assert.hasText(collectionName, ERROR_COLLECTION_STRING_EMPTY);

        ArangoCollection collection = collection(collectionName);
        MultiDocumentEntity<DocumentCreateEntity<T>> result = collection.insertDocuments(batchToSave);

        if (result.getErrors().size() > 0) {
            if (result.getDocuments().size() > 0) {
                throw new RuntimeException("Major error only some of the batch were saved! Please retry!");
            } else {
                throw new RuntimeException("Batch save failed!");
            }
        }

        if (result.getDocuments().size() != batchToSave.size()) {
            throw new RuntimeException("Major error only some of the batch were saved!");
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

    public <T> ArangoCursor<T> query(
            final String query,
            final Map<String, Object> bindVars,
            final Class<T> type) throws ArangoDBException {
        return database.query(query, bindVars, null, type);
    }

    private <T> String name(Class<T> type) {

        return type.getSimpleName().toString().toLowerCase();
    }

    private <T extends Entity> void updateFields(T object, DocumentEntity d) {
        object.setId(d.getId());
        object.setKey(d.getKey());
        object.setRev(d.getRev());
    }

}
