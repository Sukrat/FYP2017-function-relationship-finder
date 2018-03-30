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

    @Parameter(names = {"--functcheck"}, description = "if you want functional check as well", order = 2)
    private boolean functionCheck = false;

    @Parameter(names = {"--normalise", "--normalize"}, description = "if you want to normalize and then analyse", order = 2)
    private boolean normalise = false;

    public List<File> getFiles() {
        return files;
    }

    public List<Integer> getAnalyseColumns() {
        return analyseColumns;
    }

    public boolean isFunctionCheck() {
        return functionCheck;
    }

    public boolean isNormalise() {
        return normalise;
    }
}
