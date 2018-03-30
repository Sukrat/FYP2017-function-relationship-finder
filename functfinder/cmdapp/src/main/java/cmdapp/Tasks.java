package cmdapp;

import cmdapp.tasks.DbscanTask;
import cmdapp.tasks.GridTask;
import cmdapp.tasks.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Tasks {

    public static String GRID_COMMAND = "grid";
    public static String DBSCAN_COMMAND = "dbscan";

    private String command;
    private Map<String, Task> tasks = new HashMap<>();

    @Autowired
    public Tasks(String command,
                 DbscanTask dbscanTask,
                 GridTask gridTask) {
        this.command = command;
        tasks.put(GRID_COMMAND, gridTask);
        tasks.put(DBSCAN_COMMAND, dbscanTask);
    }

    public void run() {
        if (tasks.containsKey(command)) {
            Task task = tasks.get(command);
            task.run();
        } else {
            throw new CmdException("No task specified");
        }
    }
}
