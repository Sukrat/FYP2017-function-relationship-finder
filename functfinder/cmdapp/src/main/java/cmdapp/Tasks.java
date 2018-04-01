package cmdapp;

import cmdapp.tasks.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Tasks {

    public static String GRID_COMMAND = "grid";
    public static String DBSCAN_COMMAND = "dbscan";
    public static String CONTINUOUS_COMMAND = "cont";

    private String command;
    private Map<String, Task> tasks = new HashMap<>();

    @Autowired
    public Tasks(String command,
                 DbscanTask dbscanTask,
                 GridTask gridTask, ContinuousTask continuousTask) {
        this.command = command;
        tasks.put(GRID_COMMAND, gridTask);
        tasks.put(DBSCAN_COMMAND, dbscanTask);
        tasks.put(CONTINUOUS_COMMAND, continuousTask);
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
