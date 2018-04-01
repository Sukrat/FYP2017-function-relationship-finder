package core.service;

import com.arangodb.ArangoCursor;
import core.model.Data;

import java.util.Collection;
import java.util.Map;

public interface IDataService {
    Data findAny();

    Data findAnyByFileName(String fileName);

    ArangoCursor<Data> findAllByFileName(String fileName);

    Collection<Data> insert(Collection<Data> datas);

    long removeByFileName(String fileName);

    ArangoCursor<String> findAllFileNames();

    <T> ArangoCursor<T> query(String query, Map<String, Object> bindVars, Class<T> entity);

    void ensureSkipListIndex(Collection<String> fields);

    String collectionName();

    Long count();

    String join(String... s);
}
