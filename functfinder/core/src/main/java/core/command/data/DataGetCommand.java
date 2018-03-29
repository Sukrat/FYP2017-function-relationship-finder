package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.Command;
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
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class DataGetCommand implements Command<String, ByteArrayOutputStream> {

    private CsvService csvService;
    private DataService dataService;

    @Autowired
    public DataGetCommand(CsvService csvService, DataService dataService) {
        this.csvService = csvService;
        this.dataService = dataService;
    }

    public ByteArrayOutputStream execute(CommandProgess progess, String fileName) {
        Data sample = dataService.findAnyByFileName(fileName);
        if (sample == null) {
            return new ByteArrayOutputStream();
        }
        ArangoCursor<Data> datas = dataService.findAllByFileName(fileName);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(sample.getRawColumns().size());
        ByteArrayOutputStream outputStream = csvService.toCsv(datas, false,
                params.getKey(), params.getValue(),
                (elem) -> elem.getRawColumns()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue())));
        return outputStream;
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
