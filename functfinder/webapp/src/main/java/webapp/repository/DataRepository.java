package webapp.repository;

import com.arangodb.springframework.repository.ArangoRepository;
import webapp.model.Data;

public interface DataRepository extends ArangoRepository<Data> {

    Data findFirstByFileName(String fileName);

    Data findFirstByRawColumnsNotNull();

    Data findFirstByWorkColumnsNotNull();
}
