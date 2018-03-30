package cmdapp.argument;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.springframework.stereotype.Component;

@Component
@Parameters(commandDescription = "dbscan clustering method arguments")
public class DbScanArguments extends ExecutionArguments {

    @Parameter(names = {"--pradius"}, description = "Radius tolerance for the input parameters", order = 3)
    private Double radius = 0.0;

    @Parameter(names = {"--oradius"}, description = "Radius tolerance of the output parameters", order = 4)
    private Double outputRadius = 0.0;

    public Double getRadius() {
        return radius;
    }

    public Double getOutputRadius() {
        return outputRadius;
    }
}
