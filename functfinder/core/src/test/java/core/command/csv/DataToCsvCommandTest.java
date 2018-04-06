package core.command.csv;

import core.Faker;
import core.command.ICommandTest;
import core.model.Data;
import core.service.CsvService;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DataToCsvCommandTest extends ICommandTest {

    private DataToCsvCommand sut;

    @Test
    public void execute_ShouldWork() {
        Collection<Data> datas = Faker.nextData(10, 3);
        sut = new DataToCsvCommand(csvService,
                datas);

        ByteArrayOutputStream result = execute(sut);

        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test
    public void execute_whenEmptyShouldReturnEmptyOutputStream() {
        Collection<Data> datas = null;
        sut = new DataToCsvCommand(new CsvService(),
                datas);

        ByteArrayOutputStream result = execute(sut);

        assertThat(result.size(), is(0));
    }

}