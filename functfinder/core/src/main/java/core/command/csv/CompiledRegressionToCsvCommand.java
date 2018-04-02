package core.command.csv;

import core.command.ICommand;
import core.command.IProgress;
import core.model.CompiledRegression;
import core.service.ICsvService;
import org.springframework.validation.ValidationUtils;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;

public class CompiledRegressionToCsvCommand implements ICommand<ByteArrayOutputStream> {

    private ICsvService csvService;
    private Collection<CompiledRegression> compiledRegressions;
    private boolean writeHeader = true;

    public CompiledRegressionToCsvCommand(ICsvService csvService, Collection<CompiledRegression> compiledRegressions) {
        this.csvService = csvService;
        this.compiledRegressions = compiledRegressions;
    }

    public CompiledRegressionToCsvCommand(ICsvService csvService, Collection<CompiledRegression> compiledRegressions
            , boolean writeHeader) {
        this.csvService = csvService;
        this.compiledRegressions = compiledRegressions;
        this.writeHeader = writeHeader;
    }

    @Override
    public ByteArrayOutputStream execute(IProgress progress) {
        if (compiledRegressions == null || compiledRegressions.isEmpty()) {
            return new ByteArrayOutputStream();
        }
        progress.setWork(compiledRegressions.size(), "Converting regressions to csv!");

        CellProcessor[] processors = new CellProcessor[]{
                new NotNull(new ParseInt()),
                new Optional(),
                new Optional(new ParseDouble()),
                new Optional(new ParseDouble()),
                new Optional(new ParseDouble()),
                new Optional(new ParseDouble()),

                new Optional(new ParseDouble()),
                new Optional(new ParseDouble()),

                new Optional(new ParseDouble()),
                new Optional(new ParseDouble()),
                new Optional(new ParseDouble()),
                new Optional(new ParseDouble()),

                new Optional(new ParseLong()),
                new Optional(new ParseLong()),

                new Optional(new ParseDouble()),
                new Optional(new ParseDouble())
        };
        String[] headers = new String[]{
                "parameter nos",
                "tolerance",
                "M mean", "M std_dev", "C mean", "C std_dev",
                "R^2 mean", "R^2 std_dev",
                "weighted M mean", "weighted M std_dev", "weighted C mean", "weighted C std_dev",
                "outliers n", "num of clusters",
                "avg num each cluster", "std_dev num each cluster"
        };
        ByteArrayOutputStream outputStream = csvService.toCsv(compiledRegressions, writeHeader,
                headers, processors,
                (elem) -> {
                    progress.increment();

                    HashMap<String, Object> map = new HashMap<>();
                    Object[] values = new Object[]{
                            elem.getColNo(),
                            elem.getTolerances(),
                            elem.getMeanM(), elem.getStdDevM(), elem.getMeanC(), elem.getWeightedMeanC(),
                            elem.getMeanRSq(), elem.getStdDevRSq(),
                            elem.getWeightedMeanM(), elem.getWeightedStdDevM(), elem.getWeightedMeanC(), elem.getWeightedStdDevC(),
                            elem.getNumberOfOutliers(), elem.getNumberOfClusters(),
                            elem.getAvgNumberOfPointsInCluster(), elem.getStdDevAvgNumberOfPointsInCluster()
                    };
                    for (int i = 0; i < values.length; i++) {
                        map.put(headers[i], values[i]);
                    }
                    return map;
                });
        return outputStream;
    }
}
