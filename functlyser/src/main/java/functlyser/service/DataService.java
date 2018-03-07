package functlyser.service;

import com.arangodb.ArangoCursor;
import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Data;
import functlyser.model.validator.DataValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.ArangoOperation;
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

import static java.lang.String.format;

@Component
public class DataService extends Service {

    private ArangoOperation arangoOperation;

    private ValidatorRunner<DataValidator> dataValidator;

    @Autowired
    public DataService(ArangoOperation arangoOperation,
                       ValidatorRunner<DataValidator> dataValidator) {
        this.arangoOperation = arangoOperation;
        this.dataValidator = dataValidator;
    }


    public Collection<Data> createMulti(Collection<Data> data) {
        return multiSave(data);
    }

    public Collection<Data> uploadCsv(MultipartFile file) {
        Data sampleData = arangoOperation.findAny(Data.class);

        List<Data> list = new ArrayList<>();
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            ICsvListReader mapReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);

            CellProcessor[] processors = null;
            String[] header = mapReader.getHeader(true);
            if (header == null) {
                throw new ApiException(format("File '%s' is empty! No records found!", file.getOriginalFilename()));
            }

            if (sampleData != null && sampleData.getColumns().size() != header.length) {
                throw new ApiException(format("Excel columns dont match with data already present! (Expected: %d and actual: %d)",
                        sampleData.getColumns().size(), header.length));
            }

            processors = new CellProcessor[header.length];
            Arrays.fill(processors, new NotNull(new ParseDouble()));

            if (header != null) {
                Data data = new Data();
                data.setFileName(file.getOriginalFilename());
                Double[] doubles = Arrays.asList(header)
                        .stream()
                        .map(m -> Double.parseDouble(m))
                        .toArray(Double[]::new);
                data.setColumns(Arrays.asList(doubles));
                list.add(data);
            }
            while (true) {
                List<Object> cols = mapReader.read(processors);
                if (cols == null) {
                    break;
                }

                Data data = new Data();
                data.setFileName(file.getOriginalFilename());
                Double[] doubles = cols.stream()
                        .map(m -> (Double) m)
                        .toArray(Double[]::new);
                data.setColumns(Arrays.asList(doubles));
                list.add(data);
            }
        } catch (IOException e) {
            throw new ApiException("File could not be loaded! " + e.getMessage());
        } catch (SuperCsvConstraintViolationException e) {
            throw new ApiException(e.getMessage());
        } catch (SuperCsvException e) {
            throw new ApiException(e.getMessage());
        }
        return multiSave(list);
    }

    public Resource downloadCsv(String filename) {
        String query = "FOR r in @@collection FILTER r.fileName == @filename RETURN r";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(Data.class));
        bindVar.put("filename", filename);
        ArangoCursor<Data> datas = arangoOperation.query(query, bindVar, Data.class);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(byteArrayOutputStream);
        CsvListWriter mapWriter = new CsvListWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        try {
            for (Data data : datas) {
                mapWriter.write(data.getColumns());
            }
            mapWriter.close();
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    public long delete(String filename) {
        String query = "FOR r in @@collection FILTER r.fileName == @filename REMOVE r in @@collection RETURN r";
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(Data.class));
        bindVar.put("filename", filename);
        ArangoCursor<Data> query1 = arangoOperation.query(query, bindVar, Data.class);
        return query1.asListRemaining().size();
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
