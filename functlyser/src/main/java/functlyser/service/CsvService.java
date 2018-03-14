package functlyser.service;

import functlyser.exception.ApiException;
import javafx.util.Pair;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.format;

@Component
public class CsvService {

    public <T> List<T> convert(InputStream inputStream, String[] header, CellProcessor[] processors,
                               Function<Map<String, Object>, T> converter) {
        return convert(inputStream, true, (heading) -> new Pair<>(header, processors), converter);
    }

    public <T> List<T> convert(InputStream inputStream, boolean addHeader,
                               Function<String[], Pair<String[], CellProcessor[]>> headingPeeker,
                               Function<Map<String, Object>, T> converter) {
        ICsvMapReader mapReader = null;
        List<T> list = new ArrayList<>();
        try {
            Reader reader = new InputStreamReader(inputStream);
            mapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE);

            String[] heading = mapReader.getHeader(true);
            if (heading == null) {
                throw new ApiException("No records found!");
            }
            Pair<String[], CellProcessor[]> params = headingPeeker.apply(heading);

            if (addHeader) {
                // process the columns
                List<Object> processedColumns = new ArrayList<>(heading.length);
                Util.executeCellProcessors(processedColumns, Arrays.asList(heading),
                        params.getValue(), 0, 0);

                // convert the List to a Map
                final Map<String, Object> destination = new HashMap<>(processedColumns.size());
                Util.filterListToMap(destination, params.getKey(), processedColumns);
                list.add(converter.apply(destination)); // adding to the list
            }

            Map<String, Object> map;
            while ((map = mapReader.read(params.getKey(), params.getValue())) != null) {
                list.add(converter.apply(map)); // adding to the list
            }
        } catch (IOException e) {
            throw new ApiException("File could not be loaded! " + e.getMessage());
        } catch (SuperCsvConstraintViolationException e) {
            throw new ApiException(e.getMessage());
        } catch (SuperCsvException e) {
            throw new ApiException(e.getMessage());
        } finally {
            if (mapReader != null) {
                try {
                    mapReader.close();
                } catch (IOException e) {

                }
            }
        }
        return list;
    }
}
