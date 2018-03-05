package functlyser.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import functlyser.BaseSpringTest;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public abstract class BaseControllerTest extends BaseSpringTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    protected MediaType contentType = MediaType.APPLICATION_JSON;

    public ResultActions mvcPost(String url, Object body) {
        try {
            return mockMvc.perform(post(url)
                    .contentType(contentType)
                    .content(objectMapper.writeValueAsString(body)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions mvcGet(String url) {
        try {
            return mockMvc.perform(get(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions mvcUpload(String url, MockMultipartFile file) {
        try {
            return mockMvc.perform(fileUpload(url).file(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions mvcDelete(String url) {
        try {
            return mockMvc.perform(delete(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
