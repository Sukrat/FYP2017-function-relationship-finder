package functlyser.controller;

import functlyser.Faker;
import functlyser.model.Data;
import functlyser.model.Profile;
import functlyser.model.ProfileInfo;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataControllerTest extends BaseControllerTest {

    @Test
    public void testUploadCsv() throws Exception {
        Profile profile = new Profile();
        profile.setName("sukrat-test");
        profile.setColumns(new HashMap<>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        profile.getColumns().put("col3", new ProfileInfo());
        profile.getColumns().get("col3").setIndex(2);
        mongoOperations.save(profile);
        MockMultipartFile file = new MockMultipartFile("file", "test.csv",
                "text/plain", testData().getBytes());
        String url = "/data/upload?profileId=" + profile.getId();

        ResultActions result = mvcUpload(url, file);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
        assertThat(mongoOperations.findAll(Data.class).size(), is(4));
    }

    @Test
    public void testUploadCsv_ShouldError_WhenProfileIdBlank() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv",
                "text/plain", testData().getBytes());
        String url = "/data/upload?profileId=adsf";

        ResultActions result = mvcUpload(url, file);

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
    }

    @Test
    public void testDownloadCsv() throws Exception {
        Profile profile = new Profile();
        profile.setName("sukrat-test");
        profile.setColumns(new HashMap<>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        profile.getColumns().put("col3", new ProfileInfo());
        profile.getColumns().get("col3").setIndex(2);
        mongoOperations.save(profile);
        List<Data> datas = getDataFor(profile.getId(), 3);
        mongoOperations.insert(datas, Data.class);

        ResultActions result = mvcGet("/data/download?filename=test.csv&profileId=" + profile.getId());

        result.andExpect(status().isOk())
                .andExpect(content().string(not(isEmptyOrNullString())));
    }

    @Test
    public void testDownloadCsv_WhenProfileIdIsWrong() throws Exception {
        ResultActions result = mvcGet("/data/download?filename=test.csv&profileId=5a9c8defc52940747cb92205");

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
    }

    private String testData() {
        return "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
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
}
