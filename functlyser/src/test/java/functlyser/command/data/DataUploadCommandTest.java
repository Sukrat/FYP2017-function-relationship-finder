package functlyser.command.data;

import functlyser.BaseTest;
import functlyser.Faker;
import functlyser.command.CommandException;
import functlyser.command.CommandProgess;
import functlyser.model.Data;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DataUploadCommandTest extends BaseTest {

    @Autowired
    private DataUploadCommand sut;

    private CommandProgess progess;

    private DataUploadCommand.Param param;

    @Override
    public void before() {
        super.before();
        progess = Mockito.mock(CommandProgess.class);
        param = Mockito.mock(DataUploadCommand.Param.class);
    }

    @Test
    public void shouldWork() throws IOException {
        String filename = "test.csv";
        InputStream inputStream = new ByteArrayInputStream(testCsvData().getBytes());
        Mockito.when(param.getInputStream()).thenReturn(inputStream);
        Mockito.when(param.getFileName()).thenReturn(filename);

        Long result = sut.execute(progess, param);

        assertThat(result, is(10L));
    }

    @Test(expected = CommandException.class)
    public void whenFileNameExistsThrowError() {
        String filename = "test.csv";
        InputStream inputStream = new ByteArrayInputStream(testCsvData().getBytes());
        Mockito.when(param.getInputStream()).thenReturn(inputStream);
        Mockito.when(param.getFileName()).thenReturn(filename);

        sut.execute(progess, param);
        sut.execute(progess, param);
    }

    @Test(expected = CommandException.class)
    public void whenLessColumnThanExpected() {
        operations.insert(Faker.nextData(10, 5), Data.class);
        String filename = "test.csv";
        InputStream inputStream = new ByteArrayInputStream(testCsvData().getBytes());
        Mockito.when(param.getInputStream()).thenReturn(inputStream);
        Mockito.when(param.getFileName()).thenReturn(filename);

        sut.execute(progess, param);
    }

    @Test(expected = CommandException.class)
    public void whenMoreColumnThanExpected() {
        operations.insert(Faker.nextData(10, 3), Data.class);
        String filename = "test.csv";
        InputStream inputStream = new ByteArrayInputStream(testCsvData().getBytes());
        Mockito.when(param.getInputStream()).thenReturn(inputStream);
        Mockito.when(param.getFileName()).thenReturn(filename);

        sut.execute(progess, param);
    }

    @Test(expected = CommandException.class)
    public void whenInsertingOneColumnThrowError() {
        String filename = "test.csv";
        InputStream inputStream = new ByteArrayInputStream("69.53716376\n69.53716376\n".getBytes());
        Mockito.when(param.getInputStream()).thenReturn(inputStream);
        Mockito.when(param.getFileName()).thenReturn(filename);

        sut.execute(progess, param);
    }

    private String testCsvData() {
        return "69.53716376,43.85339759,27.0789345,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938,27.0789345\n" +
                "22.25475914,86.61652591,57.5153819,27.0789345\n" +
                "48.64121873,86.71553793,31.9302133,27.0789345\n" +
                "17.92339575,29.04257876,77.9548911,27.0789345\n" +
                "29.67161912,40.04240202,37.6319701,27.0789345\n" +
                "3.654760994,26.18860114,34.1813028,27.0789345\n" +
                "62.58462797,69.04237323,3.61164518,27.0789345\n" +
                "76.30360999,78.95775159,73.4413411,27.0789345\n" +
                "88.56795563,1.081714266,11.8977555,27.0789345\n";
    }
}
