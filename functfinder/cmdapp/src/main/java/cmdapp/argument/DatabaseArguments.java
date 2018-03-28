package cmdapp.argument;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "Database initialisation commands")
public class DatabaseArguments {

    @Parameter(names = {"--databaseName"}, description = "databaseName name to access")
    private String databaseName = "functfinder";

    @Parameter(names = {"--user"}, description = "databaseName user name", order = 0)
    private String user = "root";

    @Parameter(names = {"--password"}, description = "databaseName password", order = 1)
    private String password = null;

    @Parameter(names = {"--host"}, description = "databaseName host", order = 2)
    private String host = "127.0.0.1";

    @Parameter(names = {"--port"}, description = "databaseName port", order = 3)
    private int port = 8529;

    @Parameter(names = {"--maxConnections"}, description = "databaseName max number of connections", order = 4)
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
}
