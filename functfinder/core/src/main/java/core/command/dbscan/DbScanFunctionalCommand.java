package core.command.dbscan;

import com.arangodb.ArangoCursor;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.IDataService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DbScanFunctionalCommand implements ICommand<ArangoCursor<Data>> {

    private IDataService dataService;
    private Double nRadius;
    private Double oRadius;

    public DbScanFunctionalCommand(IDataService dataService, Double nRadius, Double oRadius) {
        this.dataService = dataService;
        this.nRadius = nRadius;
        this.oRadius = oRadius;
    }

    @Override
    public ArangoCursor<Data> execute(IProgress progress) {
        Data any = dataService.findAny();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }

        progress.setWork(1, "Scanning for non functional points via dbscan!");

        List<String> columns = any.getWorkColumns()
                .entrySet()
                .stream()
                .map(m -> format("workColumns.%s", m.getKey()))
                .collect(Collectors.toList());

        columns.parallelStream()
                .forEach((column) -> {
                    dataService.ensureSkipListIndex(Arrays.asList(column));
                });

        int startIndex = 1;
        int endIndex = any.getWorkColumns().size();

        String rawQuery = dataService.join(
                "LET result = (",
                "FOR r IN @@col",
                "LET neigh = (",
                "FOR ng in @@col",
                "%1$s",
                "LET dist = SQRT(%2$s)",
                "FILTER dist <= @radius",
                "FILTER ABS(ng.workColumns.col0 - r.workColumns.col0) > @outputTolerance",
                "RETURN ng)",
                "RETURN neigh",
                ")",
                "FOR i IN FLATTEN(result)",
                "RETURN DISTINCT i");

        String filter = "";
        String dist = "";
        for (int i = startIndex; i < endIndex; i++) {
            filter += format("FILTER (ng.workColumns.%1$s >= r.workColumns.%1$s - @radius\n", Data.colName(i));
            filter += format("&& ng.workColumns.%1$s <= r.workColumns.%1$s) + @radius\n", Data.colName(i));
            dist += format("POW(ng.workColumns.%1$s - r.workColumns.%1$s, 2) + \n", Data.colName(i));
        }
        dist += "0";

        String query = format(rawQuery, filter, dist);
        ArangoCursor<Data> datas = dataService.query(query, new HashMap<String, Object>() {{
            put("radius", Math.abs(nRadius));
            put("outputTolerance", Math.abs(oRadius));
        }}, Data.class);
        progress.increment();
        return datas;
    }
}
