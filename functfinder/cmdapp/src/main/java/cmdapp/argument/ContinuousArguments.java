package cmdapp.argument;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContinuousArguments extends ExecutionArguments {

    @Parameter(names = {"--fromtol", "--fromradius"}, description = "analyse tolerance start", order = 3)
    private Double fromTol = 0.0;

    @Parameter(names = {"--totol", "--toradius"}, description = "analyse tolerance end (exclusive)", order = 4)
    private Double toTol = 0.1;

    @Parameter(names = {"--increment"}, description = "increment of the tolerance", order = 4)
    private Double increment = 0.02;

    @Parameter(names = {"--grid", "-g"}, description = "grid method continuous scanning (Default is dbscan method)", order = 4)
    private boolean gridWay = false;

    public Double getFromTol() {
        return fromTol;
    }

    public Double getToTol() {
        return toTol;
    }

    public Double getIncrement() {
        return increment;
    }

    public boolean isGridWay() {
        return gridWay;
    }
}
