package core.command.csv;

import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.ICsvService;
import javafx.util.Pair;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DataToCsvCommand implements ICommand<ByteArrayOutputStream> {

    private ICsvService csvService;
    private Collection<Data> datas;

    public DataToCsvCommand(ICsvService csvService, Collection<Data> datas) {
        this.csvService = csvService;
        this.datas = datas;
    }

    @Override
    public ByteArrayOutputStream execute(IProgress progress) {
        if (datas == null || datas.isEmpty()) {
            return new ByteArrayOutputStream();
        }

        Data sample = datas.stream().findFirst().get();
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
