package functlyser.controller;

import functlyser.model.Data;
import functlyser.model.Profile;
import functlyser.model.ProfileInfo;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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


    private String testData() {
        return "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
    }


}
