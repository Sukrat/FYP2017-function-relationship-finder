package core.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CompiledRegressionTest {

    private CompiledRegression sut;
    private static final double DELTA = 0.001;

    @Test
    public void compileRegression() {
        List<Regression> regressionList = new ArrayList<Regression>() {{
            add(new Regression() {{
                setM1(20.564);
                setM2(10.0);
                setC1(105.615);
                setC2(10.0);
                setNumOfDataPoints(3L);
            }});
            add(new Regression() {{
                setM1(356.446);
                setM2(100.);
                setC1(2.256 * 2);
                setC2(2.0);
                setNumOfDataPoints(1L);
            }});
            add(new Regression() {{
                setM1(5.64564);
                setM2(1.0);
                setC1(55.0);
                setC2(0.0);
                setNumOfDataPoints(3L);
            }});
        }};

        CompiledRegression result = CompiledRegression.compiledRegression(10, regressionList, 10L);

        assertThat(result.getColNo(), is(10));
        assertThat(result.getMeanM(), is(closeTo(3.7555, DELTA)));
        assertThat(result.getStdDevM(), is(closeTo(1.4715, DELTA)));
        assertThat(result.getMeanC(), is(closeTo(4.2725, DELTA)));
        assertThat(result.getStdDevC(), is(closeTo(4.5413, DELTA)));

        assertThat(result.getWeightedMeanM(), is(closeTo(3.810082857, DELTA)));
        assertThat(result.getWeightedStdDevM(), is(closeTo(1.664518435, DELTA)));
        assertThat(result.getWeightedMeanC(), is(closeTo(4.848642857, DELTA)));
        assertThat(result.getWeightedStdDevC(), is(closeTo(5.002285476, DELTA)));

        assertThat(result.getNumberOfOutliers(), is(3L));
        assertThat(result.getNumberOfClusters(), is(3L));
        assertThat(result.getAvgNumberOfPointsInCluster(), is(closeTo(2.3333333333333, DELTA)));
        assertThat(result.getStdDevAvgNumberOfPointsInCluster(), is(closeTo(0.94280904158206, DELTA)));
    }

    @Test
    public void compileRegression_whenListEmpty() {
        List<Regression> regressionList = new ArrayList<>();

        CompiledRegression result = CompiledRegression.compiledRegression(10, regressionList, 10L);

        assertThat(result.getColNo(), is(10));
        assertTrue(result.getMeanC() == null);
        assertTrue(result.getStdDevC() == null);
        assertTrue(result.getMeanM() == null);
        assertTrue(result.getStdDevM() == null);
    }

}
