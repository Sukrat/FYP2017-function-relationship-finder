package cmdapp.tasks;

import cmdapp.CmdException;
import cmdapp.argument.ExecutionArguments;
import core.command.CommandException;
import core.command.ICommandExecutor;
import core.command.csv.CsvToDataCommand;
import core.command.data.*;
import core.model.Data;
import core.service.ICsvService;
import core.service.IDataService;

import java.io.*;
import java.util.Collection;

public abstract class Task implements Runnable {

    protected ICommandExecutor executor;
    protected IDataService dataService;
    protected ICsvService csvService;

    public Task(ICommandExecutor executor, IDataService dataService, ICsvService csvService) {
        this.executor = executor;
        this.dataService = dataService;
        this.csvService = csvService;
    }

    protected void save(ByteArrayOutputStream outputStream, String filename) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename);
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
}
