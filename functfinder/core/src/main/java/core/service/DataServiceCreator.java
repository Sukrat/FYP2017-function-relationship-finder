package core.service;

import com.arangodb.ArangoDBException;
import core.Util;
import core.arango.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataServiceCreator {

    private Operations operations;

    @Autowired
    public DataServiceCreator(Operations operations) {
        this.operations = operations;
    }

    public IDataService create(String profile) {
        if (profile == null || profile.trim().isEmpty()) {
            throw new ServiceException("Profile name cannot be null or empty!");
        }
        try {
            IDataService IDataService = new DataService(operations, Util.dataCollectionName(profile));
            return IDataService;
        } catch (ArangoDBException ex) {
            if (ex.getErrorNum() == 1208) {
                throw new ServiceException("'%s' is not allowed! Valid character are [a-zA-Z0-9_-]", profile);
            }
            throw ex;
        }
    }
}
