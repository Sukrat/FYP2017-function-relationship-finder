package core.command.data;

import com.arangodb.ArangoCursor;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;
import core.service.DataService;
import core.service.IDataService;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class DataNormalizeCommand implements ICommand<Long> {

    private IDataService dataService;

    public DataNormalizeCommand(IDataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Long execute(IProgress progress) {
        Data sample = dataService.findAny();
        if (sample == null) {
            throw new CommandException("No data found! Nothing to normalize!");
        }
        int colSize = sample.getRawColumns().size();

        progress.update(0, 2, "Getting the max and min values of each columns!");

        MaxMinCol maxMinCol = run(colSize);

        progress.update(1, 2, "Updating normalized values!");

        String rawQuery = dataService.join(
                "FOR r IN @@col",
                "UPDATE { _key: r._key, workColumns: { %1$s }",
                "} IN @@col",
                "COLLECT WITH COUNT INTO c",
                "RETURN c");

        String cols = "";
        for (int i = 0; i < colSize; i++) {
            cols += format("%1$s: (r.rawColumns.%1$s - @min%1$s) / @maxMinusMin%1$s,\n", Data.colName(i));
        }
        cols = cols.substring(0, cols.length() - 2);

        String query = format(rawQuery, cols);
        Map<String, Object> bindVar = new HashMap<>();
        for (int i = 0; i < colSize; i++) {
            Double max = maxMinCol.getColumns().get(Data.colName(i)).getMax();
            Double min = maxMinCol.getColumns().get(Data.colName(i)).getMin();
            Double maxMinusMin = max - min;
            bindVar.put(format("min%s", Data.colName(i)), min);
            bindVar.put(format("maxMinusMin%s", Data.colName(i)), maxMinusMin == 0.0 ? 1.0 : maxMinusMin);
        }
        ArangoCursor<Long> result = dataService.query(query, bindVar, Long.class);

        progress.increment();
        return result.asListRemaining().get(0);
    }

    private MaxMinCol run(int colSize) {
        String rawQuery = dataService.join(
                "FOR r IN @@col",
                "COLLECT AGGREGATE",
                "%1$s",
                "RETURN {",
                "columns: { %2$s }",
                "}");

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
        ArangoCursor<MaxMinCol> result = dataService.query(query, new HashMap<>(), MaxMinCol.class);
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
