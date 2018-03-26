package functlyser.service;

import functlyser.model.Data;
import javafx.util.Pair;
import org.springframework.core.io.Resource;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.List;

import static java.lang.String.format;

public abstract class BaseSearchService {

    protected double fixTolerance(double tolerance) {
        return tolerance == 0.0 ? 1.0 : Math.abs(tolerance);
    }

    protected Pair<String[], CellProcessor[]> getArgumentsForCsv(int size) {
        CellProcessor[] processors = new CellProcessor[size];
        String[] headers = new String[size];
        for (int i = 0; i < size; i++) {
            headers[i] = format("%s%d", Data.prefixColumn, i);
            processors[i] = new NotNull(new ParseDouble());
        }
        return new Pair<>(headers, processors);
    }
}
