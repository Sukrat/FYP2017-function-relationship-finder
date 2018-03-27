package webapp.controller;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
