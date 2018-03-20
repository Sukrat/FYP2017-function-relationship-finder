//package functlyser.service;
//
//import functlyser.Faker;
//import functlyser.exception.ApiException;
//import functlyser.model.Data;
//import functlyser.repository.DataRepository;
//import org.bson.types.ObjectId;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.junit.Assert.*;
//
//public class AnalysisServiceTest extends BaseServiceTest {
//
//    @Autowired
//    private ProfileRepository profileRepository;
//
//    @Autowired
//    private DataRepository dataRepository;
//
//    private AnalysisService sut;
//
//    @Before
//    public void before() {
//        super.before();
//        sut = new AnalysisService(mongoOperations,
//                profileRepository,
//                dataRepository, null);
//    }
//
//    @Test
//    public void testDivideIntoGrids() {
//        Profile profile = new Profile();
//        profile.setName("test");
//        profile.setRawColumns(new HashMap<>());
//        profile.getRawColumns().put("col1", new ProfileInfo());
//        profile.getRawColumns().put("col2", new ProfileInfo());
//        profile.getRawColumns().get("col2").setIndex(1);
//        profile.getRawColumns().put("col3", new ProfileInfo());
//        profile.getRawColumns().get("col3").setIndex(2);
//        mongoOperations.save(profile);
//        List<Data> datas = getDataFor(profile.getId(), 10, "test.csc");
//        mongoOperations.insert(datas, Data.class);
//        datas = getDataFor(profile.getId(), 10, "sukhi.csc");
//        mongoOperations.insert(datas, Data.class);
//
//        int result = sut.divideIntoGrids(profile.getId());
//
//        assertThat(result, is(20));
//        assertTrue(mongoOperations.findAll(Data.class)
//                .stream()
//                .allMatch(m -> m.getGridIndexes().size() == profile.getRawColumns().size()));
//    }
//
//    @Test(expected = ApiException.class)
//    public void testDivideIntoGrids_whenProfileIdIsBlank() {
//        int result = sut.divideIntoGrids("");
//    }
//
//    @Test(expected = ApiException.class)
//    public void testDivideIntoGrids_whenDataIsBlank() {
//        Profile profile = new Profile();
//        profile.setName("test");
//        mongoOperations.save(profile);
//
//        int result = sut.divideIntoGrids(profile.getId());
//    }
//
//    private List<Data> getDataFor(String profileId, int num, String filename) {
//        List<Data> list = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            Data data = new Data();
//            data.setProfileId(new ObjectId(profileId));
//            data.setFileName(filename);
//            data.setRawColumns(new HashMap<>());
//            data.getRawColumns().put("col1", (double) i);
//            data.getRawColumns().put("col2", i * Faker.nextDouble());
//            data.getRawColumns().put("col3", i * Faker.nextDouble());
//            list.add(data);
//        }
//        return list;
//    }
//}
