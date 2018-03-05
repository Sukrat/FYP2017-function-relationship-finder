package functlyser.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    @Autowired
    protected MongoOperations mongoOperations;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    protected MediaType contentType = MediaType.APPLICATION_JSON;


    @Before
    public void before() {
        Set<String> collectionNames = mongoOperations.getCollectionNames();
        for (String collectionName :
                collectionNames) {
            if (mongoOperations.collectionExists(collectionName)) {
                mongoOperations.dropCollection(collectionName);
            }
            mongoOperations.createCollection(collectionName);
        }
    }

    @After
    public void after() {
        Set<String> collectionNames = mongoOperations.getCollectionNames();
        for (String collectionName :
                collectionNames) {
            mongoOperations.dropCollection(collectionName);
        }
    }

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
}
