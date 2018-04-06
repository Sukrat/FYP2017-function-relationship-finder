package core.command.csv;

import core.command.ICommandTest;
import core.model.Data;
import core.service.CsvService;
import core.service.ServiceException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CsvToDataCommandTest extends ICommandTest {

    private CsvToDataCommand sut;

    @Test
    public void execute_ShouldWork() {
        String csv = "50,20,10\n" +
                "100,5,5";
        String fileName = "testcsv.csv";
        sut = new CsvToDataCommand(csvService,
                new ByteArrayInputStream(csv.getBytes()),
                fileName);

        Collection<Data> result = execute(sut);

        assertThat(result.size(), is(2));
        result.stream()
                .forEach(m -> {
                    assertThat(m.getRawColumns().size(), is(3));
                    assertThat(m.getWorkColumns().size(), is(3));
                    assertThat(m.getFileName(), is(fileName));
                });
    }

    @Test(expected = ServiceException.class)
    public void execute_whenEmptyShouldReturnEmptyList() {
        String csv = "";
        String fileName = "testcsv.csv";
        sut = new CsvToDataCommand(new CsvService(),
                new ByteArrayInputStream(csv.getBytes()),
                fileName);

        Collection<Data> result = execute(sut);
    }

}