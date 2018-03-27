package core.command.data;

import core.command.Command;
import core.command.CommandException;
import core.command.CommandProgess;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;
import javafx.util.Pair;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DataUploadCommand implements Command<DataUploadCommand.Param, Long> {

    private CsvService csvService;
    private DataService dataService;

    public DataUploadCommand(CsvService csvService, DataService dataService) {
        this.csvService = csvService;
        this.dataService = dataService;
    }

    public Long execute(CommandProgess progress, Param param) {
        String fileName = param.getFileName();
        progress.update(0, 2, "'%s' file upload started!", fileName);

        Data anyData = dataService.findAnyByFileName(fileName);
        if (anyData != null) {
            throw new CommandException("'%s' already exists!", fileName);
        }
        anyData = dataService.findAny();

        List<Data> datas = null;
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
        if (anyData != null) {
            int colSize = anyData.getRawColumns().size();
            datas = csvService.parse(param.getInputStream(), false,
                    header -> {
                        if (header.length != colSize) {
                            throw new CommandException("Number of columns donot match! (expected: %d, actual: %d)",
                                    colSize, header.length);
                        }
                        return getArgumentsForCsv(header.length);
                    }, converter);
        } else {
            datas = csvService.parse(param.getInputStream(), false,
                    header -> {
                        if (header.length == 1) {
                            throw new CommandException("Number of columns must be greater than 1!");
                        }
                        return getArgumentsForCsv(header.length);
                    }, converter);
        }
        progress.update(1, 2, "'%s' parsed successfully!", fileName);
        progress.update(1, 2, "'%s' inserting started!", fileName);
        dataService.insert(datas);
        progress.update(2, 2, "'%s' saved successfully!", fileName);
        return datas.stream().count();
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
        private InputStream inputStream;
        private String fileName;

        public Param(InputStream inputStream, String fileName) {
            this.inputStream = inputStream;
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }
}