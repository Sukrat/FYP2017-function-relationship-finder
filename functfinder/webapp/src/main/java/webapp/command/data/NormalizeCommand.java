package webapp.command.data;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.command.Command;
import webapp.command.CommandException;
import webapp.command.CommandProgess;
import webapp.model.Data;
import webapp.repository.DataRepository;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Component
public class NormalizeCommand implements Command<Void, Long> {

    private ArangoOperations operations;
    private DataRepository dataRepository;

    @Autowired
    public NormalizeCommand(ArangoOperations operations, DataRepository dataRepository) {
        this.operations = operations;
        this.dataRepository = dataRepository;
    }

    @Override
    public Long execute(CommandProgess progress, Void aVoid) {
        Data sample = dataRepository.findFirstByRawColumnsNotNull();
        if (sample == null) {
            throw new CommandException("Data not found!");
        }
        int colSize = sample.getRawColumns().size();

        progress.setTotalWork(2);

        progress.update("Getting the max and min values of each columns");
        MaxMinCol maxMinCol = run(colSize);
        progress.update(1, "Updating normalized values!");

        String rawQuery = "FOR r IN @@col\n" +
                "UPDATE { _key: r._key, \n" +
                "workColumns: { %1$s }\n" +
                "} IN @@col\n" +
                "COLLECT WITH COUNT INTO c\n" +
                "RETURN c\n";

        String cols = "";
        for (int i = 0; i < colSize; i++) {
            cols += format("%1$s: (r.rawColumns.%1$s - @min%1$s) / @maxMinusMin%1$s,\n", Data.colName(i));
        }
        cols = cols.substring(0, cols.length() - 2);

        String query = format(rawQuery, cols);
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", Data.class);
        for (int i = 0; i < colSize; i++) {
            Double max = maxMinCol.getColumns().get(Data.colName(i)).getMax();
            Double min = maxMinCol.getColumns().get(Data.colName(i)).getMin();
            Double maxMinusMin = max - min;
            bindVar.put(format("min%s", Data.colName(i)), min);
            bindVar.put(format("maxMinusMin%s", Data.colName(i)), maxMinusMin == 0.0 ? 1.0 : maxMinusMin);
        }
        ArangoCursor<Long> result = operations.query(query, bindVar, null, Long.class);
        return result.asListRemaining().get(0);
    }

    private MaxMinCol run(int colSize) {
        String rawQuery = "FOR r IN @@col\n" +
                "COLLECT AGGREGATE\n" +
                "%1$s\n" +
                "RETURN {\n" +
                "columns: { %2$s }" +
                "}\n";

        String maxMin = "";
        for (int i = 0; i < colSize; i++) {
            maxMin += format("max%1$s = MAX(r.rawColumns.%1$s),\n", Data.colName(i));
            maxMin += format("min%1$s = MIN(r.rawColumns.%1$s),\n", Data.colName(i));
        }
        maxMin = maxMin.substring(0, maxMin.length() - 2);

        String returnValue = "";
        for (int i = 0; i < colSize; i++) {
            returnValue += format("%1$s: { max: max%1$s, min: min%1$s },\n", Data.colName(i));
        }
        returnValue = returnValue.substring(0, returnValue.length() - 2);

        String query = format(rawQuery, maxMin, returnValue);
        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@col", Data.class);
        ArangoCursor<MaxMinCol> result = operations.query(query, bindVar, null, MaxMinCol.class);
        return result.asListRemaining().get(0);
    }

    public static class MaxMinCol {
        private Map<String, MaxMin> columns;

        public Map<String, MaxMin> getColumns() {
            return columns;
        }

        public void setColumns(Map<String, MaxMin> columns) {
            this.columns = columns;
        }
    }

    public static class MaxMin {
        private Double max;
        private Double min;

        public Double getMin() {
            return min;
        }

        public Double getMax() {
            return max;
        }

        public void setMin(Double min) {
            this.min = min;
        }

        public void setMax(Double max) {
            this.max = max;
        }
    }
}
