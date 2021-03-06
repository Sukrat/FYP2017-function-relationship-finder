package cmdapp.argument;


import cmdapp.FileConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Parameters
public abstract class ExecutionArguments {

    @Parameter(names = "--files", description = "List of files on which analysis needs to be done",
            required = true, converter = FileConverter.class, order = 0)
    private List<File> files = new ArrayList<>();

    @Parameter(names = {"--cols"}, description = "List of column nos. need to be analysed (-1 for all)", order = 1)
    private List<Integer> analyseColumns = new ArrayList<>(Arrays.asList(-1));

    @Parameter(names = {"--normalise", "--normalize"}, description = "if you want to normalize and then analyse", order = 2)
    private boolean normalise = false;

    @Parameter(names = {"--onNormalised", "--onNormalized"}, description = "if you want to analyse on normalized value", order = 2)
    private boolean onNormalisedValue = false;

    public List<File> getFiles() {
        return files;
    }

    public List<Integer> getAnalyseColumns() {
        return analyseColumns;
    }

    public boolean isNormalise() {
        return normalise;
    }

    public boolean isOnNormalisedValue() {
        return onNormalisedValue;
    }
}
