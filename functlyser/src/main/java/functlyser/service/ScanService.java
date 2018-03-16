package functlyser.service;

import com.arangodb.ArangoCursor;
import functlyser.exception.ApiException;
import functlyser.model.Data;
import functlyser.model.GridData;
import functlyser.model.validator.DataValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.ArangoOperation;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class ScanService extends BaseSearchService {

    private ArangoOperation arangoOperation;

    private ValidatorRunner<DataValidator> dataValidator;

    private CsvService csvService;

    @Autowired
    public ScanService(ArangoOperation arangoOperation, ValidatorRunner<DataValidator> dataValidator, CsvService csvService) {
        this.arangoOperation = arangoOperation;
        this.dataValidator = dataValidator;
        this.csvService = csvService;
    }

    public Resource functionalCheck(double radius, double outputTolerance) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        List<String> columns = any.getColumns()
                .entrySet()
                .stream()
                .map(m -> format("columns.%s", m.getKey()))
                .collect(Collectors.toList());
        arangoOperation.ensureSkipListIndexMulti(Data.class, columns);

        int startIndex = 1;
        int endIndex = any.getColumns().size();

        StringBuilder builder = new StringBuilder();

        builder.append("LET result = (");
        builder.append("FOR r IN @@dataCollection\n");
        builder.append("LET neigh = (\n");
        builder.append("FOR ng in @@dataCollection\n");
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(format("FILTER (ng.columns.%1$s >= -@radius + r.columns.%1$s\n", Data.colName(i)));
            builder.append(format("&& ng.columns.%1$s <= @radius + r.columns.%1$s)\n", Data.colName(i)));
        }
        builder.append("LET dist = SQRT(\n");
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(format("POW(ng.columns.%1$s - r.columns.%1$s, 2)\n", Data.colName(i)));
            if (i < endIndex - 1) {
                builder.append("+ \n");
            }
        }
        builder.append(")\n");
        builder.append("FILTER dist <= @radius\n");
        builder.append("FILTER ABS(ng.columns.col0 - r.columns.col0) > @outputTolerance\n");
        builder.append("RETURN ng\n");
        builder.append(")\n");
        builder.append("RETURN neigh\n");
        builder.append(")\n");

        builder.append("FOR i IN FLATTEN(result)\n");
        builder.append("RETURN DISTINCT i\n");
        builder.append("\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@dataCollection", arangoOperation.collectionName(Data.class));
        bindVar.put("radius", Math.abs(radius));
        bindVar.put("outputTolerance", Math.abs(outputTolerance));
        ArangoCursor<Data> datas = arangoOperation.query(builder.toString(), bindVar, Data.class);

        Pair<String[], CellProcessor[]> params = getArgumentsForCsv(any.getColumns().size());
        return csvService.convert(datas, false, params.getKey(), params.getValue(), (elem) -> {
            return elem.getColumns()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
        });
    }

    public Resource analyseParameter(int column) {
        return null;
    }

}
