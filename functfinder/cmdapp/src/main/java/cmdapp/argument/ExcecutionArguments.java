package cmdapp.argument;


import cmdapp.FileConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Parameters
public abstract class ExcecutionArguments {

    @Parameter(names = "--files", description = "files on which analysis to be done", required = true, converter = FileConverter.class, order = 0)
    private List<File> files = new ArrayList<>();

    @Parameter(names = {"--cols"}, description = "Enter columns need to be analysed", order = 1)
    private List<Integer> analyseColumns = new ArrayList<>(Arrays.asList(-1));

    @Parameter(names = {"--functcheck"}, description = "if you want functional check as well", order = 2)
    private boolean functionCheck = false;

    public List<File> getFiles() {
        return files;
    }

    public List<Integer> getAnalyseColumns() {
        return analyseColumns;
    }

    public boolean isFunctionCheck() {
        return functionCheck;
    }
}
