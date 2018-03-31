package cmdapp.tasks;

import cmdapp.CmdCommandExecutor;
import cmdapp.CmdException;
import cmdapp.CmdProgress;
import cmdapp.argument.ExecutionArguments;
import core.command.CommandException;
import core.command.csv.CsvToDataCommand;
import core.command.data.*;
import core.model.Data;
import core.service.CsvService;
import core.service.DataService;

import java.io.*;
import java.util.Collection;

public abstract class Task implements Runnable {

    protected CmdCommandExecutor cmdCommandExecutor;
    protected DataService dataService;
    protected CsvService csvService;

    public Task(CmdCommandExecutor cmdCommandExecutor, DataService dataService, CsvService csvService) {
        this.cmdCommandExecutor = cmdCommandExecutor;
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
                    Collection<Data> datas = cmdCommandExecutor.execute(
                            new CsvToDataCommand(
                                    csvService,
                                    in, file.getName()));
                    cmdCommandExecutor.execute(new DataInsertCommand(
                            dataService,
                            datas
                    ));
                });
        if (args.isNormalise()) {
            cmdCommandExecutor.execute(new DataNormalizeCommand(
                    dataService
            ));
        }
    }

    protected void cleanup() {
        Collection<String> fileNames = cmdCommandExecutor.execute(new DataGetFileNamesCommand(
                dataService
        ));
        fileNames.stream().forEach(filename -> new DataDeleteByFileNameCommand(
                dataService, filename
        ));
    }
}
