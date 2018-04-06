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

public interface Task extends Runnable {

}
