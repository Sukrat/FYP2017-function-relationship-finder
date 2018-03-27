package webapp.controller;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import webapp.BaseTest;
import webapp.Faker;
import webapp.command.CommandException;
import webapp.command.CommandProgess;
import webapp.model.Data;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataControllerTest extends BaseControllerTest {

    @Test
    public void uploadCsvFile_shouldUpload() throws Exception {
        String url = "/data/upload";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv",
                "text/plain", testData().getBytes());

        ResultActions result = mvcUpload(url, file);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
        assertThat(Lists.newArrayList(operations.findAll(Data.class)).size(), is(4));
    }

    @Test
    public void uploadCsvFile_withEmptyFileShouldThrow() throws Exception {
        String url = "/data/upload";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv",
                "text/plain", "".getBytes());

        ResultActions result = mvcUpload(url, file);

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
    }

    @Test
    public void listFileNames_() throws Exception {
        String url = "/data/filenames";

        ResultActions result = mvcGet(url);

        result.andExpect(status().isOk())
                .andExpect(content().string(not(isEmptyOrNullString())));
    }

    @Test
    public void downloadCsv() throws Exception {
        List<Data> datas = Faker.nextData("test.csv", 3, 7);
        operations.insert(datas, Data.class);

        ResultActions result = mvcGet("/data/download?fileName=test.csv");

        result.andExpect(status().isOk())
                .andExpect(content().string(not(isEmptyOrNullString())));
    }

    @Test
    public void delete() throws Exception {
        List<Data> datas = Faker.nextData("test.csv", 3, 7);
        operations.insert(datas, Data.class);

        ResultActions result = mvcDelete("/data/delete?fileName=test.csv");

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
    }

    private String testData() {
        return "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
    }
}
