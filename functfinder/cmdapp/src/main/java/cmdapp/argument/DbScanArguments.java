package cmdapp.argument;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "dbscan clustering method arguments")
public class DbScanArguments extends ExcecutionArguments {

    @Parameter(names = {"--pradius"},description = "radius for the input parameters",  order = 3)
    private Double radius = 0.0;

    @Parameter(names = {"--oradius"},description = "radius of the output parameters",  order = 4)
    private Double outputRadius = 0.0;

    public Double getRadius() {
        return radius;
    }

    public Double getOutputRadius() {
        return outputRadius;
    }
}
