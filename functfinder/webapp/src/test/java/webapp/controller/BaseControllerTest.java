package webapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import webapp.BaseTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public abstract class BaseControllerTest extends BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void before() {
        super.before();
    }

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

    public ResultActions mvcDelete(String url, Object body) {
        try {
            return mockMvc.perform(delete(url)
                    .contentType(contentType)
                    .content(objectMapper.writeValueAsString(body)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
