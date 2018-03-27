package webapp.command.data;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import webapp.command.CommandException;
import webapp.command.ResultCommand;
import webapp.model.Data;
import webapp.repository.DataRepository;
import webapp.service.CsvService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class DataGetCommand implements ResultCommand<String, Resource> {

    private CsvService csvService;
    private ArangoOperations operations;
    private DataRepository dataRepository;

    @Autowired
    public DataGetCommand(CsvService csvService, ArangoOperations operations, DataRepository dataRepository) {
        this.csvService = csvService;
        this.operations = operations;
        this.dataRepository = dataRepository;
    }

    @Override
    public Resource execute(String fileName) {
        Data sample = dataRepository.findFirstByFileName(fileName);
        if (sample == null) {
            throw new CommandException("Data not found!");
        }

        String query = "FOR r in @@col FILTER r.fileName == @filename RETURN r";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", Data.class);
        bindVar.put("filename", fileName);
        ArangoCursor<Data> datas = operations.query(query, bindVar, null, Data.class);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(sample.getRawColumns().size());
        Resource resource = csvService.convert(datas, false, params.getKey(), params.getValue(), (elem) -> {
            return elem.getRawColumns()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
        });
        return resource;
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
