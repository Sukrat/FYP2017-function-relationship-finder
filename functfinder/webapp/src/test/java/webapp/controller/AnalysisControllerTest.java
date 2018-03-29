//package webapp.controller;
//
//import org.junit.Test;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class AnalysisControllerTest extends BaseControllerTest {
//
////    @Test
////    public void gridCluster() throws Exception {
////        List<Double> tolerance = Arrays.asList(5.0, 5.0, 5.0, 5.0, 5.0);
////
////        ResultActions resultActions = mvcPost("/analysis/grid/cluster", tolerance);
////
////        resultActions.andExpect(status().isOk())
////                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
////    }
////
////    @Test
////    public void gridCluster_ShouldGetErrorWhenToleranceColumnMismatch() throws Exception {
////        List<Double> tolerance = Arrays.asList(5.0, 5.0, 5.0, 5.0);
////
////        ResultActions resultActions = mvcPost("/analysis/grid/cluster", tolerance);
////
////        resultActions.andExpect(status().isBadRequest())
////                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
////    }
//}
