package functlyser.controller;

import functlyser.Faker;
import functlyser.model.Data;
import functlyser.model.GridData;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AnalysisControllerTest extends BaseControllerTest {

    @Test
    public void gridCluster() throws Exception {
        List<Double> tolerance = Arrays.asList(5.0, 5.0, 5.0, 5.0, 5.0);
        List<Data> datas = Faker.nextData("test.csc", 10, 6);
        arangoOperation.insert(datas, Data.class);
        datas = Faker.nextData("test.csc", 10, 6);
        arangoOperation.insert(datas, Data.class);

        ResultActions resultActions = mvcPost("/analysis/grid/cluster", tolerance);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
    }

    @Test
    public void gridCluster_ShouldGetErrorWhenToleranceColumnMismatch() throws Exception {
        List<Double> tolerance = Arrays.asList(5.0, 5.0, 5.0, 5.0);
        List<Data> datas = Faker.nextData("test.csc", 10, 4);
        arangoOperation.insert(datas, Data.class);
        datas = Faker.nextData("test.csc", 10, 4);
        arangoOperation.insert(datas, Data.class);

        ResultActions resultActions = mvcPost("/analysis/grid/cluster", tolerance);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", not(isEmptyOrNullString())));
    }

//    @Test
//    public void testIsFunction() throws Exception {
//        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
//        arangoOperation.insert(perfectDataFor, Data.class);
//        GridData gridData = getPerfectGroupedData(Arrays.asList(4l, 5l, 6l));
//        arangoOperation.insert(gridData);
//        gridData = getPerfectGroupedData(Arrays.asList(5l, 6l, 7l));
//        arangoOperation.insert(gridData);
//
//        ResultActions resultActions = mvcPost("/analysis/grid/isfunction", 1);
//
//        resultActions.andExpect(status().isOk());
//    }
//
//    private List<Data> getPerfectDataFor(int num, String filename, int numColumn) {
//        List<Data> list = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            Data data = new Data();
//            data.setFileName(filename);
////            data.setColumns(new ArrayList<>());
//            for (int j = 0; j < numColumn; j++) {
////                data.getColumns().add(j + Faker.nextDouble() + i / 5);
//            }
//            list.add(data);
//        }
//        return list;
//    }
//
//    private GridData getPerfectGroupedData(List<Long> gridIndex) {
//        GridData gridData = new GridData();
//        gridData.setBoxIndex(gridIndex);
//        List<Data> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Data data = new Data();
//            data.setFileName("asdf");
////            data.setColumns(new ArrayList<>());
////            data.getColumns().add(Faker.nextDouble());
//            for (int j = 0; j < gridIndex.size(); j++) {
////                data.getColumns().add(gridIndex.get(j) + Faker.nextDouble());
//            }
//            list.add(data);
//        }
////        gridData.setMembers(list);
//        return gridData;
//    }
}
