package core.command.grid;

import com.arangodb.ArangoCursor;
import core.command.Command;
import core.command.CommandException;
import core.command.CommandProgess;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class GridFunctionCheckCommand implements Command<GridFunctionCheckCommand.Param, ByteArrayOutputStream> {

    private DataService dataService;
    private CsvService csvService;

    @Autowired
    public GridFunctionCheckCommand(DataService dataService, CsvService csvService) {
        this.dataService = dataService;
        this.csvService = csvService;
    }

    @Override
    public ByteArrayOutputStream execute(CommandProgess progress, Param tols) {
        Data any = dataService.findAny();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        List<Double> tolerances = new ArrayList<>(tols.getTolerances());
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
            put("outputTol", fixTolerance(tols.getOutputTolerance()));
        }}, Data.class);
        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(any.getRawColumns().size());
        return csvService.toCsv(datas, false, params.getKey(), params.getValue(), (elem) -> {
            return elem.getRawColumns()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
        });
    }

    private double fixTolerance(double tolerance) {

        return tolerance == 0.0 ? 1.0 : Math.abs(tolerance);
    }

    private Pair<String[], CellProcessor[]> getArgumentsForCsv(int size) {
        CellProcessor[] processors = new CellProcessor[size];
        String[] headers = new String[size];
        for (int i = 0; i < size; i++) {
            headers[i] = format("%s%d", Data.prefixColumn, i);
            processors[i] = new NotNull(new ParseDouble());
        }
        return new Pair<>(headers, processors);
    }

    public static class Param {
        private Double outputTolerance;
        private List<Double> tolerances;

        public Param(Double outputTolerance, List<Double> tolerances) {
            this.outputTolerance = outputTolerance;
            this.tolerances = tolerances;
        }

        public Double getOutputTolerance() {
            return outputTolerance;
        }

        public List<Double> getTolerances() {
            return tolerances;
        }
    }
}
