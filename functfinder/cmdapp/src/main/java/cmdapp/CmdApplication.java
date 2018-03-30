package cmdapp;

import cmdapp.argument.DatabaseArguments;
import cmdapp.argument.DbScanArguments;
import cmdapp.argument.GridArguments;
import cmdapp.argument.HelpArguments;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CmdApplication {

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");

        DatabaseArguments databaseArguments = new DatabaseArguments();
        HelpArguments helpArguments = new HelpArguments();
        GridArguments gridArguments = new GridArguments();
        DbScanArguments dbScanArguments = new DbScanArguments();

        JCommander jCommander = new JCommander.Builder()
                .addObject(databaseArguments)
                .addObject(helpArguments)
                .addCommand(Tasks.GRID_COMMAND, gridArguments)
                .addCommand(Tasks.DBSCAN_COMMAND, dbScanArguments)
                .build();
        jCommander.setProgramName("functfinder");
        AnnotationConfigApplicationContext context = null;
        try {


            jCommander.parse(args);
            if (helpArguments.isHelp()) {
                jCommander.usage();
                return;
            }
            context = new AnnotationConfigApplicationContext();
            context.registerShutdownHook();
            context.getBeanFactory().registerResolvableDependency(DatabaseArguments.class, databaseArguments);
            context.getBeanFactory().registerResolvableDependency(HelpArguments.class, helpArguments);
            context.getBeanFactory().registerResolvableDependency(GridArguments.class, gridArguments);
            context.getBeanFactory().registerResolvableDependency(DbScanArguments.class, dbScanArguments);
            context.getBeanFactory().registerSingleton("command", jCommander.getParsedCommand());
            context.register(CoreConfiguration.class);
            context.refresh();
            Tasks tasks = context.getBean(Tasks.class);
            tasks.run();
        } catch (ParameterException ex) {
            jCommander.usage();
            System.out.println(String.format("ERROR: ", ex.getMessage()));
        } catch (Exception ex) {
            System.out.println(String.format("ERROR: ", ex.getMessage()));
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }
}
