package cmdapp.argument;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.springframework.stereotype.Component;

@Component
@Parameters(separators = "=", commandDescription = "Database initialisation commands")
public class DatabaseArguments {

    @Parameter(names = {"--databaseName"}, description = "Name of the database")
    private String databaseName = "functfinder";

    @Parameter(names = {"--profile"}, description = "Name of the profile")
    private String profileName = "cmd";

    @Parameter(names = {"--user"}, description = "Username for accessing the database", order = 0)
    private String user = "root";

    @Parameter(names = {"--password"}, description = "Password for accessing the database", order = 1)
    private String password = null;

    @Parameter(names = {"--host"}, description = "Host name", order = 2)
    private String host = "127.0.0.1";

    @Parameter(names = {"--port"}, description = "Port", order = 3)
    private int port = 8529;

    @Parameter(names = {"--maxConnections"}, description = "Max number of connections allowed to the database", order = 4)
    private int maxConnections = 8;

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getProfileName() {
        return profileName;
    }
}
