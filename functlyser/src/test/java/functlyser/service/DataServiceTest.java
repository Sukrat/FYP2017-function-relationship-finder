package functlyser.service;

import functlyser.Faker;
import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Data;
import functlyser.model.validator.DataValidator;
import functlyser.model.validator.ValidatorRunner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.hibernate.validator.internal.util.StringHelper.isNullOrEmptyString;
import static org.mockito.Matchers.anyObject;

public class DataServiceTest extends BaseServiceTest {

    @Mock
    private ValidatorRunner<DataValidator> dataValidator;

    @Mock
    private Errors errors;

    @Autowired
    private CsvService csvService;

    private DataService sut;

    @Before
    public void before() {
        super.before();
        sut = new DataService(arangoOperation, dataValidator, csvService);
    }

    @Test
    public void insertCsvFile_shouldWork() {
        MultipartFile file = new MockMultipartFile("test.csv", testCsvData().getBytes());
        Mockito.when(dataValidator.validate(anyObject())).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(false);

        Collection<Data> multi = sut.insertCsvFile(file);

        assertThat(multi.size(), is(10));
        assertTrue(multi.stream().allMatch(m -> !isNullOrEmptyString(m.getId())));
        assertTrue(multi.stream().allMatch(m -> m.getColumns().size() == 3));
    }

    @Test(expected = ApiException.class)
    public void insertCsvFile_whenFileNameExistsThrowError() {
        MultipartFile file = new MockMultipartFile("test.csv", testCsvData().getBytes());
        Mockito.when(dataValidator.validate(anyObject())).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(false);

        sut.insertCsvFile(file);
        sut.insertCsvFile(file);
    }

    @Test(expected = ApiException.class)
    public void insertCsvFile_whenDataIsEmpty() {
        MultipartFile file = new MockMultipartFile("test.csv", "".getBytes());

        Collection<Data> multi = sut.insertCsvFile(file);
    }

    @Test(expected = ApiException.class)
    public void insertCsvFile_whenLessColumnThanExpected() {
        arangoOperation.insert(Faker.nextData(10, 5), Data.class);
        MultipartFile file = new MockMultipartFile("test.csv", testCsvData().getBytes());

        Collection<Data> multi = sut.insertCsvFile(file);
    }

    @Test(expected = ApiException.class)
    public void insertCsvFile_whenMoreColumnThanExpected() {
        arangoOperation.insert(Faker.nextData(10, 2), Data.class);
        MultipartFile file = new MockMultipartFile("test.csv", testCsvData().getBytes());

        Collection<Data> multi = sut.insertCsvFile(file);
    }

    @Test
    public void getCsvFile() throws IOException {
        arangoOperation.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);
        arangoOperation.insert(Faker.nextData("hello.csv", 10, 3),
                Data.class);

        Resource multi = sut.getCsvFile("hello.csv");
        assertThat(multi.contentLength(), greaterThan(0L));
    }

    @Test
    public void getCsvFile_whenFileIsNotThere() throws IOException {
        arangoOperation.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);

        Resource multi = sut.getCsvFile("hello.csv");
        assertThat(multi.contentLength(), is(0L));
    }

    @Test(expected = ApiException.class)
    public void getCsvFile_whenDataIsNotPresent() throws IOException {
        Resource multi = sut.getCsvFile("test.csv");

        assertThat(multi.contentLength(), is(0L));
    }

        @Test
    public void deleteCsvFile() throws IOException {
        arangoOperation.insert(Faker.nextData("test.csv", 10, 3),
                Data.class);
            arangoOperation.insert(Faker.nextData("black.csv", 10, 3),
                    Data.class);


        long result = sut.deleteCsvFile("test.csv");

        assertThat(result, is(10L));
        assertThat(arangoOperation.findAll(Data.class).asListRemaining().size(), is(10));
    }

    @Test
    public void deleteCsvFile_whenFilenameNotPresent() throws IOException {
        long result = sut.deleteCsvFile("test.csv");

        assertThat(result, is(0L));
    }

    @Test
    public void listCsvFileNames() throws IOException {
        arangoOperation.insert(Faker.nextData("testcsv.csv", 10, 3),
                Data.class);
        arangoOperation.insert(Faker.nextData("hello.csv", 10, 3),
                Data.class);
        arangoOperation.insert(Faker.nextData("asfdsf.csv", 10, 3),
                Data.class);
        List<String> result = sut.listCsvFileNames();

        assertThat(result.size(), is(3));
        assertThat(result, containsInAnyOrder("testcsv.csv", "hello.csv", "asfdsf.csv"));
    }

    @Test
    public void listCsvFileNames_whenEmpty() throws IOException {
        List<String> result = sut.listCsvFileNames();

        assertThat(result.size(), is(0));
    }

    private String testCsvData() {
        return "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n" +
                "17.92339575,29.04257876,77.9548911\n" +
                "29.67161912,40.04240202,37.6319701\n" +
                "3.654760994,26.18860114,34.1813028\n" +
                "62.58462797,69.04237323,3.61164518\n" +
                "76.30360999,78.95775159,73.4413411\n" +
                "88.56795563,1.081714266,11.8977555\n";
    }
}
