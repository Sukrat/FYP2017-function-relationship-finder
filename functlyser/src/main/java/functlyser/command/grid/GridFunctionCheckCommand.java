package functlyser.command.grid;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import functlyser.command.Command;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import functlyser.model.GridData;
import functlyser.repository.DataRepository;
import functlyser.repository.GridDataRepository;
import functlyser.service.CsvService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class GridFunctionCheckCommand implements Command<Double, Resource> {


    private ArangoOperations operations;
    private DataRepository dataRepository;
    private GridDataRepository gridDataRepository;
    private CsvService csvService;

    @Autowired
    public GridFunctionCheckCommand(ArangoOperations operations, DataRepository dataRepository,
                                    GridDataRepository gridDataRepository, CsvService csvService) {
        this.operations = operations;
        this.dataRepository = dataRepository;
        this.gridDataRepository = gridDataRepository;
        this.csvService = csvService;
    }

    @Override
    public Resource execute(CommandProgess progress, Double outputTolerances) {
        Data any = dataRepository.findFirstByWorkColumnsNotNull();
        if (any == null) {
            throw new CommandException("No data found in the database!");
        }
        if (gridDataRepository.count() == 0) {
            throw new CommandException("Data has not been clustered!");
        }
        progress.setTotalWork(1, "Going through each cluster to check its functional relation!");
        String query = "LET result = FLATTEN(\n"
                + "FOR r IN @@gridCol\n"
                + "FILTER COUNT(r.members) > 1\n"
                + "LET grouped_y = \n"
                + "( FOR member IN r.members\n"
                + "LET elem = FIRST(\n"
                + "FOR d in @@dataCol FILTER d._id == member LIMIT 1 RETURN d\n"
                + ")\n"
                + "COLLECT y = FLOOR(elem.workColumns.col0 / @tolerance) INTO elems = elem\n"
                + "RETURN elems )\n"
                + "FILTER COUNT(grouped_y) > 1\n"
                + "RETURN FLATTEN(grouped_y)\n"
                + ")\n"
                + "FOR r in result\n"
                + "RETURN r\n";

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@gridCol", GridData.class);
        bindVar.put("@dataCol", Data.class);
        bindVar.put("tolerance", fixTolerance(outputTolerances));
        ArangoCursor<Data> datas = operations.query(query, bindVar, null, Data.class);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(any.getRawColumns().size());
        return csvService.convert(datas, false, params.getKey(), params.getValue(), (elem) -> {
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

}
