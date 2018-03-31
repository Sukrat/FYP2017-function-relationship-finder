package core.command.csv;

import core.command.ICommandTest;
import core.model.CompiledRegression;
import core.service.CsvService;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CompiledRegressionToCsvCommandTest extends ICommandTest {

    private CompiledRegressionToCsvCommand sut;

    @Test
    public void execute() {
        Collection<CompiledRegression> regressions = Arrays.asList(new CompiledRegression());
        sut = new CompiledRegressionToCsvCommand(new CsvService(),
                regressions);

        ByteArrayOutputStream result = execute(sut);

        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test
    public void execute_whenEmptyList() {
        Collection<CompiledRegression> regressions = Arrays.asList();
        sut = new CompiledRegressionToCsvCommand(new CsvService(),
                regressions);

        ByteArrayOutputStream result = execute(sut);

        assertThat(result.size(), is(0));
    }
}