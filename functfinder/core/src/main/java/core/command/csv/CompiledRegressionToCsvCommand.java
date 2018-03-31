package core.command.csv;

import core.command.ICommand;
import core.command.IProgress;
import core.model.CompiledRegression;
import core.service.ICsvService;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CompiledRegressionToCsvCommand implements ICommand<ByteArrayOutputStream> {

    private ICsvService csvService;
    private Collection<CompiledRegression> compiledRegressions;

    public CompiledRegressionToCsvCommand(ICsvService csvService, Collection<CompiledRegression> compiledRegressions) {
        this.csvService = csvService;
        this.compiledRegressions = compiledRegressions;
    }

    @Override
    public ByteArrayOutputStream execute(IProgress progress) {
        if (compiledRegressions == null || compiledRegressions.isEmpty()) {
            return new ByteArrayOutputStream();
        }
        progress.setWork(compiledRegressions.size(), "Converting regressions to csv!");

        ByteArrayOutputStream outputStream = csvService.toCsv(compiledRegressions, true,
                new String[]{"colNo", "meanM", "stdDevM", "weightedMeanM", "weightedStdDevM",
                        "meanC", "stdDevC", "weightedMeanC", "weightedStdDevC"},
                new CellProcessor[]{new NotNull(new ParseInt()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble()),
                        new Optional(new ParseDouble())},
                (elem) -> {
                    progress.increment();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("colNo", elem.getColNo());
                    map.put("meanM", elem.getMeanM());
                    map.put("stdDevM", elem.getStdDevM());
                    map.put("meanC", elem.getMeanC());
                    map.put("stdDevC", elem.getStdDevC());

                    map.put("weightedMeanM", elem.getWeightedMeanM());
                    map.put("weightedStdDevM", elem.getWeightedStdDevM());
                    map.put("weightedMeanC", elem.getWeightedMeanC());
                    map.put("weightedStdDevC", elem.getWeightedStdDevC());

                    return map;
                });
        return outputStream;
    }
}
