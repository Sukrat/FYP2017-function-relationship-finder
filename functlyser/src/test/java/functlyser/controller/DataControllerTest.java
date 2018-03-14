package functlyser.controller;

import functlyser.Faker;
import functlyser.model.Data;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
        assertThat(arangoOperation.findAll(Data.class).asListRemaining().size(), is(4));
    }

    @Test
    public void uploadCsvFile_withEmptyFileShouldThrow() throws Exception {
        String url = "/data/upload";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv",
                "text/plain", "".getBytes());

        ResultActions result = mvcUpload(url, file);

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
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
        arangoOperation.insert(datas, Data.class);

        ResultActions result = mvcGet("/data/download?filename=test.csv");

        result.andExpect(status().isOk())
                .andExpect(content().string(not(isEmptyOrNullString())));
    }
//
//    @Test
//    public void testDelete() throws Exception {
//        List<Data> datas = getPerfectDataFor(3, "test.csv", 7);
//        arangoOperation.insert(datas, Data.class);
//
//        ResultActions result = mvcDelete("/data/delete?filename=test.csv");
//
//        result.andExpect(status().isOk())
//                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
//    }

    private String testData() {
        return "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
    }
//
//    private List<Data> getPerfectDataFor(int num, String filename, int numColumn) {
//        List<Data> list = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            Data data = new Data();
//            data.setFileName(filename);
////            data.setColumns(new ArrayList<>());
//            for (int j = 0; j < numColumn; j++) {
////                data.getColumns().add(j + Faker.nextDouble());
//            }
//            list.add(data);
//        }
//        return list;
//    }
}
