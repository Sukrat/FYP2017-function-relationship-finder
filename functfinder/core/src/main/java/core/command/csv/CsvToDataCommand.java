package core.command.csv;

import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.ICsvService;
import javafx.util.Pair;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class CsvToDataCommand implements ICommand<Collection<Data>> {

    private ICsvService csvService;
    private InputStream csv;
    private String fileName;

    public CsvToDataCommand(ICsvService csvService, InputStream csv, String fileName) {
        this.csvService = csvService;
        this.csv = csv;
        this.fileName = fileName;
    }

    @Override
    public Collection<Data> execute(IProgress progress) {
        Function<Map<String, Object>, Data> converter = (row) -> {
            Data data = new Data();
            data.setFileName(fileName);
            Map<String, Double> collect = row.entrySet()
                    .stream()
                    .collect(Collectors
                            .toMap(m -> m.getKey(), m -> (Double) m.getValue()));
            data.setRawColumns(collect);
            data.setWorkColumns(collect);
            return data;
        };
        progress.setWork(1, "Parsing csv file '%s'!", fileName);

        List<Data> datas = csvService.parse(csv, false,
                header -> getArgumentsForCsv(header.length), converter);

        progress.increment();
        return datas;
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
