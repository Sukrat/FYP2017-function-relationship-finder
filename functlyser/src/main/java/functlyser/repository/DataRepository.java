package functlyser.repository;

import functlyser.model.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends MongoRepository<Data, String> {

    List<Data> findAllByFileName(String fileName);

    void deleteAllByProfileIdAndFileName(ObjectId profileId, String fileName);
}
