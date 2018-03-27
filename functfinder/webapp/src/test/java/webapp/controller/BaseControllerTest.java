package webapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import webapp.BaseTest;
import webapp.Faker;
import webapp.command.CommandException;
import webapp.command.CommandProgess;
import webapp.model.Data;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public abstract class BaseControllerTest extends BaseTest {

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
