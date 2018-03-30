package core.service;

import com.arangodb.ArangoDBException;
import core.arango.Operations;
import core.model.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataServiceCreator {

    private Operations operations;

    @Autowired
    public DataServiceCreator(Operations operations) {
        this.operations = operations;
    }

    public IDataService create(String prefix) {
        try {
            DataService dataService = new DataService(operations, prefix);
            return dataService;
        } catch (ArangoDBException ex) {
            if (ex.getErrorNum() == 1208) {
                throw new ServiceException("'%s' is not allowed! Valid character are [a-zA-Z0-9_-]", prefix);
            }
            throw ex;
        }
    }
}
