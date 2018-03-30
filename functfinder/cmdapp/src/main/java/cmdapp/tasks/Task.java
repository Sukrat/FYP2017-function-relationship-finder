package cmdapp.tasks;

import cmdapp.CmdException;
import cmdapp.CmdProgress;
import cmdapp.argument.ExecutionArguments;
import core.command.CommandException;
import core.command.data.DataUploadCommand;
import core.command.data.DeleteDataCommand;
import core.command.data.ListFileNamesCommand;
import core.command.data.NormalizeCommand;

import java.io.*;

public abstract class Task implements Runnable {

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

    protected void insert(ExecutionArguments args, DataUploadCommand command, NormalizeCommand normalizeCommand) {
        args.getFiles()
                .stream()
                .forEach(file -> {
                    InputStream in;
                    try {
                        in = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new CmdException(e.getMessage());
                    }
                    command.execute(new CmdProgress(),
                            new DataUploadCommand.Param(in, file.getName()));
                });
        if (args.isNormalise()) {
            normalizeCommand.execute(new CmdProgress(), null);
        }
    }

    protected void cleanup(ListFileNamesCommand listFileNamesCommand, DeleteDataCommand deleteDataCommand) {
        listFileNamesCommand.execute(new CmdProgress(), null)
                .stream()
                .forEach(filename -> {
                    deleteDataCommand.execute(new CmdProgress(), filename);
                });
    }
}
