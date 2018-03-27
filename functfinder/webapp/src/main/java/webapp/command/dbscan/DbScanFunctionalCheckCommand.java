package webapp.command.dbscan;


import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.CollectionOperations;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import webapp.command.Command;
import webapp.command.CommandException;
import webapp.command.CommandProgess;
import webapp.model.Data;
import webapp.model.GridData;
import webapp.repository.DataRepository;
import webapp.repository.GridDataRepository;
import webapp.service.CsvService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class DbScanFunctionalCheckCommand implements Command<DbScanFunctionalCheckCommand.Param, Resource> {


    private ArangoOperations operations;
    private DataRepository dataRepository;
    private CsvService csvService;

    @Autowired
    public DbScanFunctionalCheckCommand(ArangoOperations operations, DataRepository dataRepository,
                                        CsvService csvService) {
        this.operations = operations;
        this.dataRepository = dataRepository;
        this.csvService = csvService;
    }

    @Override
    public Resource execute(CommandProgess progress, Param param) {
        Data any = dataRepository.findFirstByWorkColumnsNotNull();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        List<String> columns = any.getWorkColumns()
                .entrySet()
                .stream()
                .map(m -> format("workColumns.%s", m.getKey()))
                .collect(Collectors.toList());

        CollectionOperations dataCollection = operations.collection(Data.class);
        columns.parallelStream()
                .forEach((column) -> {
                    dataCollection.ensureSkiplistIndex(Arrays.asList(column), null);
                });


        int startIndex = 1;
        int endIndex = any.getWorkColumns().size();

        String rawQuery = "LET result = (\n"
                + "FOR r IN @@dataCol\n"
                + "LET neigh = (\n"
                + "FOR ng in @@dataCol\n"
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
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@dataCol", Data.class);
        bindVar.put("radius", Math.abs(param.getRadius()));
        bindVar.put("outputTolerance", Math.abs(param.getTolerance()));
        ArangoCursor<Data> datas = operations.query(query, bindVar, null, Data.class);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(any.getRawColumns().size());
        return csvService.convert(datas, false, params.getKey(), params.getValue(), (elem) -> {
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