//package functlyser.service;
//
//import com.arangodb.ArangoDatabase;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.stereotype.Component;
//
//import static java.lang.String.format;
//
//@Component
//public class AnalysisService extends Service {
//
//    private MongoOperations mongoOperations;
//
//    private ArangoDatabase arangoDatabase;
//
//    @Autowired
//    public AnalysisService(MongoOperations mongoOperations, DataRepository dataRepository,
//                           ArangoDatabase arangoDatabase) {
//        this.mongoOperations = mongoOperations;
//        this.dataRepository = dataRepository;
//        this.arangoDatabase = arangoDatabase;
//    }
//
//    public int divideIntoGrids(String profileId) {
////        Profile profile = profileRepository.findOne(profileId);
////        if (profile == null) {
////            throw new ApiException(format("Profile with id:'%s' not found!", profileId));
////        }
////
////        List<Data> datas = dataRepository.findAllByProfileId(new ObjectId(profileId));
////        if (datas == null || datas.isEmpty()) {
////            throw new ApiException("Profile data is empty!");
////        }
////
////        BulkOperations bulk = mongoOperations.bulkOps(BulkOperations.BulkMode.UNORDERED, Data.class);
////        List<Pair<Query, Update>> updates = datas.parallelStream()
////                .map(data -> {
////                    Map<String, Double> values = data.getColumns();
////                    Query query = new Query(Criteria.where("_id").is(new ObjectId(data.getId())));
////                    Update update = new Update();
////                    for (Map.Entry<String, ProfileInfo> column : profile.getColumns().entrySet()) {
////                        Double tolerance = 1.0;
////                        if (column.getValue().getTolerance() != 0.0) {
////                            tolerance = column.getValue().getTolerance();
////                        }
////                        Double value = values.get(column.getKey());
////                        long gridIndex = (long) (value / tolerance);
////                        update.set("gridIndexes." + column.getKey(), gridIndex);
////                    }
////                    return Pair.of(query, update);
////                }).collect(Collectors.toList());
////        bulk.updateOne(updates);
////        BulkWriteResult save = bulk.execute();
////        return save.getModifiedCount();
//        return 0;
//    }
//}
