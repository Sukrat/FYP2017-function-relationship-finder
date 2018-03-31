package core.command.grid;

import com.arangodb.ArangoCursor;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;
import core.service.IDataService;
import javafx.util.Pair;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class GridFunctionCommand implements ICommand<ArangoCursor<Data>> {

    private IDataService dataService;
    private List<Double> nTolerances;
    private Double oTolerance;

    public GridFunctionCommand(IDataService dataService, List<Double> nTolerances, Double oTolerance) {
        this.dataService = dataService;
        this.nTolerances = nTolerances;
        this.oTolerance = oTolerance;
    }

    @Override
    public ArangoCursor<Data> execute(IProgress progress) {
        Data any = dataService.findAny();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        List<Double> tolerances = new ArrayList<>(nTolerances);
        if (tolerances.size() == 1) {
            double tolerance = tolerances.get(0);
            // as one of the tolerances is already in the list
            for (int i = 1; i < any.getWorkColumns().size() - 1; i++) {
                tolerances.add(tolerance);
            }
        } else if (any.getWorkColumns().size() - 1 != tolerances.size()) {
            throw new CommandException(
                    "Number of tolerance must be equal to the data columns or enter one tolerance for all (expected: %d actual: %d)",
                    any.getWorkColumns().size() - 1, tolerances.size());
        }

        progress.update(0, 1, "Going through each cluster to check its functional relation!");
        String rawQuery = dataService.join(
                "LET result = FLATTEN(",
                "FOR r IN @@col",
                "COLLECT index = [ %1$s ]",
                "INTO members = r",
                "FILTER COUNT(members) > 1",
                "LET grouped_y = ",
                "( FOR elem IN members",
                "COLLECT y = FLOOR(elem.workColumns.col0 / @outputTol) INTO elems = elem",
                "RETURN elems )",
                "FILTER COUNT(grouped_y) > 1",
                "RETURN FLATTEN(grouped_y)",
                ")",
                "FOR r in result",
                "RETURN r");
        // ignoring first tolerance as that is for ouput column
        String cols = "";
        for (int i = 1; i < any.getWorkColumns().size(); i++) {
            cols += format("FLOOR(r.workColumns.%1$s / @tolerance%2$d),\n", Data.colName(i), i);
        }
        cols = cols.substring(0, cols.length() - 2);
        String query = format(rawQuery, cols);
        ArangoCursor<Data> datas = dataService.query(query, new HashMap<String, Object>() {{
            // ignoring first tolerance as that is for ouput column
            int tolIndex = 0;
            for (int i = 1; i < any.getWorkColumns().size(); i++) {
                put(format("tolerance%1$d", i), fixTolerance(tolerances.get(tolIndex++)));
            }
            put("outputTol", fixTolerance(oTolerance));
        }}, Data.class);
        return datas;
    }

    private double fixTolerance(double tolerance) {

        return tolerance == 0.0 ? 1.0 : Math.abs(tolerance);
    }
}
