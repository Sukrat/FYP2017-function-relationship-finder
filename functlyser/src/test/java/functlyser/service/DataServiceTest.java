package functlyser.service;

import functlyser.Faker;
import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Data;
import functlyser.model.Profile;
import functlyser.model.ProfileInfo;
import functlyser.model.validator.DataValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.DataRepository;
import functlyser.repository.ProfileRepository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.hibernate.validator.internal.util.StringHelper.isNullOrEmptyString;
import static org.mockito.Matchers.anyObject;

public class DataServiceTest extends BaseServiceTest {

    @Autowired
    private DataRepository dataRepository;

    @Mock
    private ValidatorRunner<DataValidator> dataValidator;

    @Autowired
    private ProfileRepository profileRepository;

    @Mock
    private Errors errors;

    private DataService sut;

    @Before
    public void before() {
        super.before();
        sut = new DataService(dataRepository, dataValidator, profileRepository);
    }

    @Test
    public void testCreateMulti() {
        int num = 10;
        List<Data> dataList = getPerfectData(num);
        Mockito.when(dataValidator.validate(anyObject())).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(false);

        List<Data> multi = sut.createMulti(dataList);

        assertTrue(multi.stream()
                .allMatch(m -> !isNullOrEmptyString(m.getId())));
        assertThat(mongoOperations.findAll(Data.class).size(), equalTo(num));
    }

    @Test(expected = ValidationException.class)
    public void testCreateMulti_WhenDataIsNotValid() {
        int num = 10;
        List<Data> dataList = getPerfectData(num);
        Mockito.when(dataValidator.validate(anyObject())).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(true);

        List<Data> multi = sut.createMulti(dataList);
    }

    @Test(expected = ApiException.class)
    public void testCreateMulti_WhenDataIsNull() {
        List<Data> dataList = null;

        List<Data> multi = sut.createMulti(dataList);
    }

    @Test(expected = ApiException.class)
    public void testCreateMulti_WhenDataIsEmpty() {
        List<Data> dataList = new ArrayList<>();

        List<Data> multi = sut.createMulti(dataList);
    }

    @Test
    public void testUploadCsv() {
        Profile profile = new Profile();
        profile.setName("sukrat-test");
        profile.setColumns(new HashMap<>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        profile.getColumns().put("col3", new ProfileInfo());
        profile.getColumns().get("col3").setIndex(2);
        mongoOperations.save(profile);
        MultipartFile file = new MockMultipartFile("test.csv", testData().getBytes());
        Mockito.when(dataValidator.validate(anyObject())).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(false);

        List<Data> multi = sut.uploadCsv(profile.getId(), file);

        assertThat(multi.size(), is(10));
        assertTrue(multi.stream().allMatch(m -> !isNullOrEmptyString(m.getId())));
    }

    @Test(expected = ApiException.class)
    public void testUploadCsv_whenProfileIdIsNotPresent() {
        String profileId = (new ObjectId()).toHexString();
        MultipartFile file = new MockMultipartFile("test.csv", testData().getBytes());

        List<Data> multi = sut.uploadCsv(profileId, file);
    }

    @Test(expected = ApiException.class)
    public void testUploadCsv_whenDataIsEmpty() {
        String profileId = (new ObjectId()).toHexString();
        MultipartFile file = new MockMultipartFile("test.csv", "".getBytes());

        List<Data> multi = sut.uploadCsv(profileId, file);
    }

    @Test(expected = ApiException.class)
    public void testUploadCsv_whenLessColumnThanExpected() {
        Profile profile = new Profile();
        profile.setName("sukrat-test");
        profile.setColumns(new HashMap<>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        mongoOperations.save(profile);
        MultipartFile file = new MockMultipartFile("test.csv", testData().getBytes());

        List<Data> multi = sut.uploadCsv(profile.getId(), file);
    }

    @Test(expected = ApiException.class)
    public void testUploadCsv_whenMoreColumnThanExpected() {
        Profile profile = new Profile();
        profile.setName("sukrat-test");
        profile.setColumns(new HashMap<>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        profile.getColumns().put("col3", new ProfileInfo());
        profile.getColumns().get("col3").setIndex(2);
        profile.getColumns().put("col4", new ProfileInfo());
        profile.getColumns().get("col4").setIndex(3);
        mongoOperations.save(profile);
        MultipartFile file = new MockMultipartFile("test.csv", testData().getBytes());

        List<Data> multi = sut.uploadCsv(profile.getId(), file);
    }

    @Test
    public void testDownloadCsv() throws IOException {
        Profile profile = new Profile();
        profile.setName("sukrat-test");
        profile.setColumns(new HashMap<>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        profile.getColumns().put("col3", new ProfileInfo());
        profile.getColumns().get("col3").setIndex(2);
        mongoOperations.save(profile);
        List<Data> datas = getDataFor(profile.getId(), 20);
        mongoOperations.insert(datas, Data.class);

        Resource multi = sut.downloadCsv(profile.getId(), "test.csv");
        assertThat(multi.contentLength(), greaterThan(0L));
    }

    @Test(expected = ApiException.class)
    public void testDownloadCsv_whenProfileIdIsNotPresent() throws IOException {
        Resource multi = sut.downloadCsv("5a9c2061c529401e74584c5f", "test.csv");
    }

    @Test(expected = ApiException.class)
    public void testDownloadCsv_whenFileNameIsNotPresent() throws IOException {
        Profile profile = new Profile();
        profile.setName("sukrat-test");
        profile.setColumns(new HashMap<>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        mongoOperations.save(profile);
        Resource multi = sut.downloadCsv(profile.getId(), "test.csv");
    }

    private List<Data> getPerfectData(int num) {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Data data = new Data();
            data.setProfileId(new ObjectId("5a9c2061c529401e74584c5f"));
            data.setFileName("sukhi.csv");
            data.setColumns(new HashMap<>());
            data.getColumns().put("col1", Faker.nextDouble());
            data.getColumns().put("col2", Faker.nextDouble());
            list.add(data);
        }
        return list;
    }

    private List<Data> getDataFor(String profileId, int num) {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Data data = new Data();
            data.setProfileId(new ObjectId(profileId));
            data.setFileName("test.csv");
            data.setColumns(new HashMap<>());
            data.getColumns().put("col1", Faker.nextDouble());
            data.getColumns().put("col2", Faker.nextDouble());
            data.getColumns().put("col3", Faker.nextDouble());
            list.add(data);
        }
        return list;
    }

    private String testData() {
        return "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n" +
                "17.92339575,29.04257876,77.9548911\n" +
                "29.67161912,40.04240202,37.6319701\n" +
                "3.654760994,26.18860114,34.1813028\n" +
                "62.58462797,69.04237323,3.61164518\n" +
                "76.30360999,78.95775159,73.4413411\n" +
                "88.56795563,1.081714266,11.8977555\n";
    }


}
