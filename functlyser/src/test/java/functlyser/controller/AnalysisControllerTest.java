package functlyser.controller;

import functlyser.Faker;
import functlyser.model.Data;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AnalysisControllerTest extends BaseControllerTest {

    @Test
    public void testGrid() throws Exception {
//        Profile profile = new Profile();
//        profile.setName("test");
//        profile.setColumns(new HashMap<>());
//        profile.getColumns().put("col1", new ProfileInfo());
//        profile.getColumns().put("col2", new ProfileInfo());
//        profile.getColumns().get("col2").setIndex(1);
//        profile.getColumns().put("col3", new ProfileInfo());
//        profile.getColumns().get("col3").setIndex(2);
//        mongoOperations.save(profile);
//        List<Data> datas = getDataFor(profile.getId(), 10, "test.csc");
//        mongoOperations.insert(datas, Data.class);
//        datas = getDataFor(profile.getId(), 10, "sukhi.csc");
//        mongoOperations.insert(datas, Data.class);
//
//        ResultActions resultActions = mvcGet("/analysis/grid?profileId=" + profile.getId());
//
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
    }


//    private List<Data> getDataFor(String profileId, int num, String filename) {
//        List<Data> list = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            Data data = new Data();
//            data.setFileName(filename);
//            data.getColumns().put("col1", (double) i);
//            data.getColumns().put("col2", i * Faker.nextDouble());
//            data.getColumns().put("col3", i * Faker.nextDouble());
//            list.add(data);
//        }
//        return list;
//    }
}
