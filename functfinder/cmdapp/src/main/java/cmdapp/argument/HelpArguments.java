package cmdapp.argument;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.springframework.stereotype.Component;

@Component
@Parameters(commandDescription = "Help arguments")
public class HelpArguments {

    @Parameter(names = {"--help", "-?"}, description = "Show list of commands and their default values")
    private boolean help = false;

    public boolean isHelp() {
        return help;
    }
}
