package functlyser.service;

import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Data;
import functlyser.model.Profile;
import functlyser.model.validator.DataValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.DataRepository;
import functlyser.repository.ProfileRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.*;

import static java.lang.String.format;

@Component
public class DataService extends Service {

    private DataRepository dataRepository;

    private ProfileRepository profileRepository;

    private ValidatorRunner<DataValidator> dataValidator;

    @Autowired
    public DataService(DataRepository dataRepository,
                       ValidatorRunner<DataValidator> dataValidator,
                       ProfileRepository profileRepository) {
        this.dataRepository = dataRepository;
        this.dataValidator = dataValidator;
        this.profileRepository = profileRepository;
    }


    public List<Data> createMulti(List<Data> data) {
        return multiSave(data);
    }

    public List<Data> uploadCsv(String profileId, MultipartFile file) {
        Profile profile = profileRepository.findOne(profileId);
        if (profile == null) {
            throw new ApiException(format("Profile with id:'%s' not found!", profileId));
        }

        List<Data> list = new ArrayList<>();
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            ICsvMapReader mapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE);

            String[] headers = orderedHeaders(profile);
            CellProcessor[] processors = new CellProcessor[headers.length];
            Arrays.fill(processors, new NotNull(new ParseDouble()));

            ObjectId objectId = new ObjectId(profile.getId());
            while (true) {
                Map<String, Object> map = mapReader.read(headers, processors);
                if (map == null) {
                    break;
                }

                Map<String, Double> temp = new HashMap<>();
                map.entrySet().stream()
                        .forEach(m -> temp.put(m.getKey(), (Double) m.getValue()));

                Data data = new Data();
                data.setProfileId(objectId);
                data.setFileName(file.getOriginalFilename());
                data.setColumns(temp);
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

    public Resource downloadCsv(String profileId, String filename) {
        Profile profile = profileRepository.findOne(profileId);
        if (profile == null) {
            throw new ApiException(format("Profile with id:'%s' not found!", profileId));
        }
        Data eg = new Data();
        eg.setProfileId(new ObjectId(profile.getId()));
        eg.setFileName(filename);

        List<Data> datas = dataRepository.findAll(Example.of(eg));
        if (datas == null || datas.isEmpty()) {
            throw new ApiException(format("Data for file '%s' not found!", filename));
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(byteArrayOutputStream);
        ICsvMapWriter mapWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        String[] headers = orderedHeaders(profile);
        CellProcessor[] processors = new CellProcessor[headers.length];
        Arrays.fill(processors, new NotNull(new ParseDouble()));

        try {
            for (Data data : datas) {
                mapWriter.write(data.getColumns(), headers, processors);
            }
            mapWriter.close();
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    public long delete(String profileId, String filename) {
        Profile profile = profileRepository.findOne(profileId);
        if (profile == null) {
            throw new ApiException(format("Profile with id:'%s' not found!", profileId));
        }

        Data eg = new Data();
        eg.setProfileId(new ObjectId(profile.getId()));
        eg.setFileName(filename);
        long count = dataRepository.count(Example.of(eg));
        if (count == 0) {
            throw new ApiException(format("Data for file '%s' not found!", filename));
        }
        dataRepository.deleteAllByProfileIdAndFileName(new ObjectId(profile.getId()), filename);
        return count;
    }

    private String[] orderedHeaders(Profile profile) {
        return profile.getColumns().entrySet()
                .stream()
                .sorted(Comparator.comparingInt(m -> m.getValue().getIndex()))
                .map(m -> m.getKey())
                .toArray(String[]::new);
    }

    private List<Data> multiSave(List<Data> data) {
        if (data == null || data.isEmpty()) {
            throw new ApiException("Data cannot be empty or null");
        }
        for (Data elem : data) {
            Errors errors = dataValidator.validate(elem);
            if (errors.hasErrors()) {
                throw new ValidationException(errors);
            }
        }
        List<Data> save = dataRepository.save(data);
        return save;
    }
}
