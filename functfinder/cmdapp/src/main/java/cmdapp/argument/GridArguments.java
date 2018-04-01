package cmdapp.argument;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Parameters(commandDescription = "grid clustering method arguments")
public class GridArguments extends ExecutionArguments {

    @Parameter(names = {"--ptol"}, description = "Tolerances of the parameters", order = 3)
    private List<Double> parameterTolerances = new ArrayList<>(Arrays.asList(1.0));

    @Parameter(names = {"--otol"}, description = "Tolerance of the output parameter", order = 4)
    private Double outputTolerances = 1.0;

    @Parameter(names = {"--functcheck"}, description = "if you want functional check as well", order = 2)
    private boolean functionCheck = false;

    public List<Double> getParameterTolerances() {
        return parameterTolerances;
    }

    public Double getOutputTolerances() {
        return outputTolerances;
    }

    public boolean isFunctionCheck() {
        return functionCheck;
    }
}
