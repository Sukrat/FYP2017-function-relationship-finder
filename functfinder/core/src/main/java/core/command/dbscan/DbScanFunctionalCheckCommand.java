package core.command.dbscan;


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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class DbScanFunctionalCheckCommand implements Command<DbScanFunctionalCheckCommand.Param, ByteArrayOutputStream> {

    private DataService dataService;
    private CsvService csvService;

    @Autowired
    public DbScanFunctionalCheckCommand(DataService dataService, CsvService csvService) {
        this.dataService = dataService;
        this.csvService = csvService;
    }

    @Override
    public ByteArrayOutputStream execute(CommandProgess progress, Param param) {
        Data any = dataService.findAny();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
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

        String rawQuery = "LET result = (\n"
                + "FOR r IN @@col\n"
                + "LET neigh = (\n"
                + "FOR ng in @@col\n"
                + "%1$s\n"
                + "LET dist = SQRT(%2$s)\n"
                + "FILTER dist <= @radius\n"
                + "FILTER ABS(ng.workColumns.col0 - r.workColumns.col0) > @outputTolerance\n"
                + "RETURN ng)\n"
                + "RETURN neigh\n"
                + ")\n"
                + "FOR i IN FLATTEN(result)\n"
                + "RETURN DISTINCT i\n";

        String filter = "";
        String dist = "";
        for (int i = startIndex; i < endIndex; i++) {
            filter += format("FILTER (ng.workColumns.%1$s >= -@radius + r.workColumns.%1$s\n", Data.colName(i));
            filter += format("&& ng.workColumns.%1$s <= @radius + r.workColumns.%1$s)\n", Data.colName(i));
            dist += format("POW(ng.workColumns.%1$s - r.workColumns.%1$s, 2) + \n", Data.colName(i));
        }
        dist += "0";

        String query = format(rawQuery, filter, dist);
        ArangoCursor<Data> datas = dataService.query(query, new HashMap<String, Object>() {{
            put("radius", Math.abs(param.getRadius()));
            put("outputTolerance", Math.abs(param.getTolerance()));
        }}, Data.class);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(any.getRawColumns().size());
        return csvService.toCsv(datas, false, params.getKey(), params.getValue(), (elem) -> {
            return elem.getRawColumns()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
        });
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
        private Double radius;
        private Double tolerance;

        public Param(Double radius, Double tolerance) {
            this.radius = radius;
            this.tolerance = tolerance;
        }

        public Double getRadius() {
            return radius;
        }

        public Double getTolerance() {
            return tolerance;
        }
    }

}