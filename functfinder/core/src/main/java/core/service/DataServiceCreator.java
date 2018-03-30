package core.service;

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

    public IDataService create(String prefix) {
        return new DataService(operations, prefix);
    }
}
