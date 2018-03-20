package functlyser.service;

import javafx.util.Pair;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

import java.io.*;
import java.util.*;
import java.util.function.Function;

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
                throw new ServiceException("No records found!");
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
            throw new ServiceException("File could not be loaded! " + e.getMessage());
        } catch (SuperCsvConstraintViolationException e) {
            throw new ServiceException(e.getMessage());
        } catch (SuperCsvException e) {
            throw new ServiceException(e.getMessage());
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

    public <T> Resource convert(Iterable<T> from, boolean writeHeader, String[] header, CellProcessor[] processors,
                                Function<T, Map<String, Object>> converter) {
        ICsvMapWriter mapWriter = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Writer writer = new OutputStreamWriter(byteArrayOutputStream);
            mapWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE);

            // write the header
            if (writeHeader) {
                mapWriter.writeHeader(header);
            }

            for (T elem : from) {
                Map<String, Object> row = converter.apply(elem);
                mapWriter.write(row, header, processors);
            }
        } catch (IOException e) {
            throw new ServiceException("Writing on file failed! " + e.getMessage());
        } catch (SuperCsvConstraintViolationException e) {
            throw new ServiceException(e.getMessage());
        } catch (SuperCsvException e) {
            throw new ServiceException(e.getMessage());
        } finally {
            if (mapWriter != null) {
                try {
                    mapWriter.close();
                } catch (IOException e) {

                }
            }
        }
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }
}
