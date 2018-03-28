package cmdapp.argument;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Parameters(commandDescription = "grid clustering method arguments")
public class GridArguments extends ExcecutionArguments {

    @Parameter(names = {"--ptol"}, description = "tolerances of the parameters", order = 3)
    private List<Double> parameterTolerances = new ArrayList<>(Arrays.asList(1.0));

    @Parameter(names = {"--otol"}, description = "tolerance of the output parameter", order = 4)
    private Double outputTolerances = 1.0;

    public List<Double> getParameterTolerances() {
        return parameterTolerances;
    }

    public Double getOutputTolerances() {
        return outputTolerances;
    }
}
