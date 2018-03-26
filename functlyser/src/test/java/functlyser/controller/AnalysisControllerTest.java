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
        operations.insert(datas, Data.class);
        datas = Faker.nextData("test.csc", 10, 6);
        operations.insert(datas, Data.class);

        ResultActions resultActions = mvcPost("/analysis/grid/cluster", tolerance);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
    }

    @Test
    public void gridCluster_ShouldGetErrorWhenToleranceColumnMismatch() throws Exception {
        List<Double> tolerance = Arrays.asList(5.0, 5.0, 5.0, 5.0);
        List<Data> datas = Faker.nextData("test.csc", 10, 4);
        operations.insert(datas, Data.class);
        datas = Faker.nextData("test.csc", 10, 4);
        operations.insert(datas, Data.class);

        ResultActions resultActions = mvcPost("/analysis/grid/cluster", tolerance);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
    }
}
