package functlyser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import functlyser.model.Profile;
import functlyser.model.ProfileInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProfileControllerTest extends BaseControllerTest {

    @After
    public void after() {
        mongoOperations.dropCollection(Profile.class);
    }

    @Test
    public void testCreateApi_ShouldReturnProfile() throws Exception {
        Profile profile = getPerfectProfile();

        ResultActions result = mvcPost("/profile/create", profile);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", not(isEmptyOrNullString())));
    }

    @Test
    public void testCreateApi_ShouldReturnError_whenDataInvalid() throws Exception {
        Profile profile = getPerfectProfile();
        profile.setName("");

        ResultActions result = mvcPost("/profile/create", profile);

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ValidationException")))
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
    }

    private Profile getPerfectProfile() {
        Profile profile = new Profile();
        profile.setName("test");
        profile.setOutputColumn("col2");
        profile.setColumns(new HashMap<String, ProfileInfo>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        profile.getColumns().put("col3", new ProfileInfo());
        profile.getColumns().get("col3").setIndex(2);
        return profile;
    }
}
