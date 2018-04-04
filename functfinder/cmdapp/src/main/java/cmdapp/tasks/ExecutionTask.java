package cmdapp.tasks;

import cmdapp.CmdException;
import cmdapp.argument.ExecutionArguments;
import core.command.CommandException;
import core.command.ICommandExecutor;
import core.command.csv.CsvToDataCommand;
import core.command.data.DataDeleteByFileNameCommand;
import core.command.data.DataGetFileNamesCommand;
import core.command.data.DataInsertCommand;
import core.command.data.DataNormalizeCommand;
import core.model.Data;
import core.service.ICsvService;
import core.service.IDataService;

import java.io.*;
import java.util.Collection;

public abstract class ExecutionTask implements Task {

    protected ICommandExecutor executor;
    protected IDataService dataService;
    protected ICsvService csvService;
    private ExecutionArguments executionArguments;

    public ExecutionTask(ICommandExecutor executor,
                         IDataService dataService,
                         ICsvService csvService,
                         ExecutionArguments executionArguments) {
        this.executor = executor;
        this.dataService = dataService;
        this.csvService = csvService;
        this.executionArguments = executionArguments;
    }

    protected void insert(ExecutionArguments args) {
        args.getFiles()
                .stream()
                .forEach(file -> {
                    InputStream in;
                    try {
                        in = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new CmdException(e.getMessage());
                    }
                    Collection<Data> datas = executor.execute(
                            new CsvToDataCommand(
                                    csvService,
                                    in, file.getName()));
                    executor.execute(new DataInsertCommand(
                            dataService,
                            datas
                    ));
                });
        if (args.isNormalise()) {
            executor.execute(new DataNormalizeCommand(
                    dataService
            ));
        }
    }

    protected void cleanup() {
        Collection<String> fileNames = executor.execute(new DataGetFileNamesCommand(
                dataService
        ));
        fileNames.stream().forEach(filename -> {
            executor.execute(new DataDeleteByFileNameCommand(
                    dataService, filename
            ));
        });
    }

    public void run() {
        cleanup();
        insert(executionArguments);
        afterInsertionRun();
        cleanup();
    }

    protected abstract void afterInsertionRun();

    protected void save(ByteArrayOutputStream outputStream, String filename) {
        FileOutputStream fileOutputStream = null;
        try {
            String prefix = executionArguments.isOnNormalisedValue() ? "on-norm" : "";
            fileOutputStream = new FileOutputStream(prefix + filename);
            outputStream.writeTo(fileOutputStream);
        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void append(ByteArrayOutputStream outputStream, String filename) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename, true);
            outputStream.writeTo(fileOutputStream);
        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
