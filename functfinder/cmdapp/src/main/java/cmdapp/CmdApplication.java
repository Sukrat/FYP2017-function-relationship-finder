package cmdapp;

import cmdapp.argument.*;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.ParseException;

public class CmdApplication {

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");

        DatabaseArguments databaseArguments = new DatabaseArguments();
        HelpArguments helpArguments = new HelpArguments();
        GridArguments gridArguments = new GridArguments();
        DbScanArguments dbScanArguments = new DbScanArguments();
        ContinuousArguments continuousArguments = new ContinuousArguments();

        JCommander jCommander = new JCommander.Builder()
                .addObject(databaseArguments)
                .addObject(helpArguments)
                .addCommand(Tasks.GRID_COMMAND, gridArguments)
                .addCommand(Tasks.DBSCAN_COMMAND, dbScanArguments)
                .addCommand(Tasks.CONTINUOUS_COMMAND, continuousArguments)
                .build();
        jCommander.setProgramName("functfinder");
        AnnotationConfigApplicationContext context = null;
        try {
            jCommander.parse(args);
            if (helpArguments.isHelp()) {
                jCommander.usage();
                return;
            }
            if (databaseArguments.getMaxConnections() < 1) {
                throw new ParameterException("Number of connections cannot be less than 1!");
            }
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
                    Integer.toString(databaseArguments.getMaxConnections()));
            context = new AnnotationConfigApplicationContext();
            context.registerShutdownHook();
            context.getBeanFactory().registerResolvableDependency(DatabaseArguments.class, databaseArguments);
            context.getBeanFactory().registerResolvableDependency(HelpArguments.class, helpArguments);
            context.getBeanFactory().registerResolvableDependency(GridArguments.class, gridArguments);
            context.getBeanFactory().registerResolvableDependency(DbScanArguments.class, dbScanArguments);
            context.getBeanFactory().registerResolvableDependency(ContinuousArguments.class, continuousArguments);
            context.getBeanFactory().registerSingleton("command", jCommander.getParsedCommand());
            context.register(CoreConfiguration.class);
            context.refresh();
            Tasks tasks = context.getBean(Tasks.class);
            tasks.run();
        } catch (ParameterException ex) {
            jCommander.usage();
            System.out.println(String.format("ERROR: %s", ex.getMessage()));
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }
}
