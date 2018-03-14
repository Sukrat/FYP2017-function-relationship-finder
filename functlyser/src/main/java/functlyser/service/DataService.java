package functlyser.service;

import com.arangodb.ArangoCursor;
import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Data;
import functlyser.model.validator.DataValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.ArangoOperation;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class DataService extends Service {

    private ArangoOperation arangoOperation;

    private ValidatorRunner<DataValidator> dataValidator;

    private CsvService csvService;

    @Autowired
    public DataService(ArangoOperation arangoOperation,
                       ValidatorRunner<DataValidator> dataValidator, CsvService csvService) {
        this.arangoOperation = arangoOperation;
        this.dataValidator = dataValidator;
        this.csvService = csvService;
    }

    public Collection<Data> insertCsvFile(MultipartFile file) {
        if (listCsvFileNames().contains(file.getOriginalFilename())) {
            throw new ApiException(format("'%s' already exists!", file.getOriginalFilename()));
        }

        Data sampleData = arangoOperation.findAny(Data.class);
        List<Data> datas = null;
        try {
            Function<Map<String, Object>, Data> converter = (row) -> {
                Data data = new Data();
                data.setFileName(file.getOriginalFilename());
                Map<String, Double> collect = row.entrySet()
                        .stream()
                        .collect(Collectors
                                .toMap(m -> m.getKey(), m -> (Double) m.getValue()));
                data.setColumns(collect);
                return data;
            };
            if (sampleData != null) {
                datas = csvService.convert(file.getInputStream(), true, header -> {
                    if (header.length != sampleData.getColumns().size()) {
                        throw new ApiException(format("Number of columns donot match. (expected: %d, actual: %d)",
                                sampleData.getColumns().size(), header.length));
                    }
                    return getArgumentsForCsv(header.length);
                }, converter);
            } else {
                datas = csvService.convert(file.getInputStream(), true, header -> getArgumentsForCsv(header.length), converter);
            }
        } catch (IOException e) {
            throw new ApiException(format("File '%s' could not be loaded! %s", file.getOriginalFilename(), e.getMessage()));
        }
        return multiSave(datas);
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

    public Resource getCsvFile(String filename) {
        String query = "FOR r in @@collection FILTER r.fileName == @filename RETURN r";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(Data.class));
        bindVar.put("filename", filename);
        ArangoCursor<Data> datas = arangoOperation.query(query, bindVar, Data.class);

        Data sample = arangoOperation.findAny(Data.class);
        if (sample == null) {
            throw new ApiException("Data not found!");
        }

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(sample.getColumns().size());
        Resource resource = csvService.convert(datas, false, params.getKey(), params.getValue(), (elem) -> {
            return elem.getColumns()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
        });
        return resource;
    }

    public long deleteCsvFile(String filename) {
        String query = "FOR r in @@collection FILTER r.fileName == @filename REMOVE r in @@collection RETURN r";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(Data.class));
        bindVar.put("filename", filename);
        ArangoCursor<Data> query1 = arangoOperation.query(query, bindVar, Data.class);
        return query1.asListRemaining().size();
    }

    public List<String> listCsvFileNames() {
        String query = "FOR r IN @@collection RETURN DISTINCT r.fileName";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(Data.class));
        ArangoCursor<String> result = arangoOperation.query(query, bindVar, String.class);
        return result.asListRemaining();
    }

    private Collection<Data> multiSave(Collection<Data> data) {
        if (data == null || data.isEmpty()) {
            throw new ApiException("Data cannot be empty or null");
        }
        for (Data elem : data) {
            Errors errors = dataValidator.validate(elem);
            if (errors.hasErrors()) {
                throw new ValidationException(errors);
            }
        }
        Collection<Data> save = arangoOperation.insert(data, Data.class);
        return save;
    }
}
