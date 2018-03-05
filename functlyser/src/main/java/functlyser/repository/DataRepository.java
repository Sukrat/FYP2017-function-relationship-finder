package functlyser.repository;

import functlyser.model.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends MongoRepository<Data, String> {

    @Query(fields = "{ 'gridIndexes':0, 'fileName':0 }")
    List<Data> findAllByProfileId(ObjectId profileId);

    void deleteAllByProfileIdAndFileName(ObjectId profileId, String fileName);
}
