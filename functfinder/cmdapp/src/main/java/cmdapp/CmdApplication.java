package cmdapp;

import cmdapp.argument.DatabaseArguments;
import cmdapp.argument.DbScanArguments;
import cmdapp.argument.GridArguments;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class CmdApplication {

    public static void main(String[] args) {
        DatabaseArguments databaseArguments = new DatabaseArguments();
        DbScanArguments dbScanArguments = new DbScanArguments();
        GridArguments gridArguments = new GridArguments();
        JCommander jCommander = new JCommander.Builder()
                .addObject(databaseArguments)
                .addCommand("grid", gridArguments)
                .addCommand("dbscan", dbScanArguments)
                .build();
        jCommander.setProgramName("functfinder");
        try {
            jCommander.parse(args);
        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            jCommander.usage();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
