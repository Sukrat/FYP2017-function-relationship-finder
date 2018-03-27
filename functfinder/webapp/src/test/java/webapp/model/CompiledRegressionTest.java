package webapp.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CompiledRegressionTest {

    private CompiledRegression sut;

    @Test
    public void compileRegression() {
        List<Regression> regressionList = new ArrayList<Regression>() {{
            add(new Regression() {{
                setM1(20.564);
                setM2(10.0);
                setC1(105.615);
                setC2(10.0);
                setNumOfDataPoints(3l);
            }});
            add(new Regression() {{
                setM1(356.446);
                setM2(100.);
                setC1(2.256 * 2);
                setC2(2.0);
                setNumOfDataPoints(1l);
            }});
            add(new Regression() {{
                setM1(5.64564);
                setM2(1.0);
                setC1(55.0);
                setC2(0.0);
                setNumOfDataPoints(3l);
            }});
        }};

        CompiledRegression result = CompiledRegression.compiledRegression(10, regressionList);

        assertThat(result.getColNo(), is(10));
        assertThat(result.getMeanM(), is(closeTo(3.7555, .001)));
        assertThat(result.getStdDevM(), is(closeTo(1.4715, .001)));
        assertThat(result.getMeanC(), is(closeTo(4.2725, .001)));
        assertThat(result.getStdDevC(), is(closeTo(4.5413, .001)));

        assertThat(result.getWeightedMeanM(), is(closeTo(3.810082857, .001)));
        assertThat(result.getWeightedStdDevM(), is(closeTo(1.664518435, .001)));
        assertThat(result.getWeightedMeanC(), is(closeTo(4.848642857, .001)));
        assertThat(result.getWeightedStdDevC(), is(closeTo(5.002285476, .001)));
    }

    @Test
    public void compileRegression_whenListEmpty() {
        List<Regression> regressionList = new ArrayList<>();

        CompiledRegression result = CompiledRegression.compiledRegression(10, regressionList);

        assertThat(result.getColNo(), is(10));
        assertTrue(result.getMeanC() == null);
        assertTrue(result.getStdDevC() == null);
        assertTrue(result.getMeanM() == null);
        assertTrue(result.getStdDevM() == null);
    }

}
