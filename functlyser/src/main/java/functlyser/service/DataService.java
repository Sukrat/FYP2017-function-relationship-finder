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
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
