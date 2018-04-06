package core.service;

import javafx.util.Pair;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ICsvService {
    <T> List<T> parse(InputStream inputStream, String[] header, CellProcessor[] processors,
                      Function<Map<String, Object>, T> converter);

    <T> List<T> parse(InputStream inputStream, boolean hasHeader,
                      Function<String[], Pair<String[], CellProcessor[]>> headingPeeker,
                      Function<Map<String, Object>, T> converter);

    <T> ByteArrayOutputStream toCsv(Iterable<T> from,
                                    boolean writeHeader,
                                    String[] header,
                                    CellProcessor[] processors,
                                    Function<T, Map<String, Object>> converter);
}
