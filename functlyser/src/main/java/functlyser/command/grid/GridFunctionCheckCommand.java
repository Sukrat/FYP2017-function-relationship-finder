package functlyser.command.grid;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import functlyser.command.Command;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import functlyser.model.GridData;
import functlyser.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class GridFunctionCheckCommand implements Command<List<Double>, Long> {


    private ArangoOperations operations;
    private DataRepository dataRepository;

    @Autowired
    public ClusterDataCommand(ArangoOperations operations, DataRepository dataRepository) {
        this.operations = operations;
        this.dataRepository = dataRepository;
    }

    @Override
    public Long execute(CommandProgess progress, List<Double> paramTolerances) {
        Data any = dataRepository.findFirstByWorkColumnsNotNull();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        if (paramTolerances == null) {
            throw new CommandException("Tolerance is required!");
        }

        int startIndex = 1;
        int endIndex = any.getRawColumns().size();
        int numParameterColumns = any.getWorkColumns().size() - 1;

        List<Double> tolerances = new ArrayList<>(paramTolerances);
        if (tolerances.size() == 1) {
            double tolerance = tolerances.get(0);
            // as one of the tolerances is already in the list
            for (int i = startIndex + 1; i < endIndex; i++) {
                tolerances.add(tolerance);
            }
        }
        if (numParameterColumns != tolerances.size()) {
            throw new CommandException(
                    "Number of tolerance must be equal to the data columns or enter one tolerance for all (expected: %d actual: %d)",
                    numParameterColumns, tolerances.size());
        }

        progress.setTotalWork(2);

        progress.update("Deleting previous clustered data!");
        // deleting all the records in grouped data
        operations.collection(GridData.class).truncate();

        progress.update(1, "Clustering data!");

        String rawQuery = "FOR r IN @@col\n"
                + "COLLECT boxIndex = [ %1$s ]\n"
                + "INTO members = r._id\n"
                + "INSERT {\n"
                + "boxIndex: boxIndex, \n"
                + "members: members \n"
                + "} INTO @@gridCol\n"
                + "COLLECT WITH COUNT INTO c\n"
                + "RETURN c\n";

        // ignoring first tolerance as that is for ouput column
        String cols = "";
        for (int i = startIndex; i < endIndex; i++) {
            cols += format("FLOOR(r.workColumns.%1$s / @tolerance%2$d),\n", Data.colName(i), i);
        }
        cols = cols.substring(0, cols.length() - 2);

        String query = format(rawQuery, cols);
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", Data.class);
        bindVar.put("@gridCol", GridData.class);
        // ignoring first tolerance as that is for ouput column
        for (int i = startIndex; i < endIndex; i++) {
            bindVar.put(format("tolerance%1$d", i), fixTolerance(tolerances.get(i - 1)));
        }

        ArangoCursor<Long> result = operations.query(query.toString(), bindVar, null, Long.class);
        return result.asListRemaining().get(0);
    }

    private double fixTolerance(double tolerance) {

        return tolerance == 0.0 ? 1.0 : Math.abs(tolerance);
    }
}
