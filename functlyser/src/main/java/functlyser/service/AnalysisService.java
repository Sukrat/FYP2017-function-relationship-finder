package functlyser.service;

import com.mongodb.BulkWriteResult;
import functlyser.exception.ApiException;
import functlyser.model.Data;
import functlyser.model.Profile;
import functlyser.model.ProfileInfo;
import functlyser.repository.DataRepository;
import functlyser.repository.ProfileRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class AnalysisService extends Service {

    private MongoOperations mongoOperations;

    private ProfileRepository profileRepository;

    private DataRepository dataRepository;

    @Autowired
    public AnalysisService(MongoOperations mongoOperations, ProfileRepository profileRepository, DataRepository dataRepository) {
        this.mongoOperations = mongoOperations;
        this.profileRepository = profileRepository;
        this.dataRepository = dataRepository;
    }

    public int divideIntoGrids(String profileId) {
        Profile profile = profileRepository.findOne(profileId);
        if (profile == null) {
            throw new ApiException(format("Profile with id:'%s' not found!", profileId));
        }

        List<Data> datas = dataRepository.findAllByProfileId(new ObjectId(profileId));
        if (datas == null || datas.isEmpty()) {
            throw new ApiException("Profile data is empty!");
        }

        BulkOperations bulk = mongoOperations.bulkOps(BulkOperations.BulkMode.UNORDERED, Data.class);
        List<Pair<Query, Update>> updates = datas.parallelStream()
                .map(data -> {
                    Map<String, Double> values = data.getColumns();
                    Query query = new Query(Criteria.where("_id").is(new ObjectId(data.getId())));
                    Update update = new Update();
                    for (Map.Entry<String, ProfileInfo> column : profile.getColumns().entrySet()) {
                        Double tolerance = 1.0;
                        if (column.getValue().getTolerance() != 0.0) {
                            tolerance = column.getValue().getTolerance();
                        }
                        Double value = values.get(column.getKey());
                        long gridIndex = (long) (value / tolerance);
                        update.set("gridIndexes." + column.getKey(), gridIndex);
                    }
                    return Pair.of(query, update);
                }).collect(Collectors.toList());
        bulk.updateOne(updates);
        BulkWriteResult save = bulk.execute();
        return save.getModifiedCount();
    }
}
