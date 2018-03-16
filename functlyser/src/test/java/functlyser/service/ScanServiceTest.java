package functlyser.service;

import functlyser.BaseSpringTest;
import functlyser.Faker;
import functlyser.exception.ApiException;
import functlyser.model.Data;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class ScanServiceTest extends BaseSpringTest {

    @Autowired
    private ScanService sut;

    @Test
    public void functionalCheck() throws IOException {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        arangoOperation.insert(perfectDataFor, Data.class);

        Resource result = sut.functionalCheck(2.0, 5.0);

        assertThat(result.contentLength(), is(0l));
    }

    @Test
    public void functionalCheck_WhereItIsNotAFunction() throws IOException {
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        arangoOperation.insert(perfectDataFor, Data.class);

        Resource result = sut.functionalCheck(2.0, 1.0);

        Assert.assertThat(result.contentLength(), greaterThan(0L));
    }

    @Test(expected = ApiException.class)
    public void functionalCheck_NoDataShouldThrow() {

        Resource result = sut.functionalCheck(2.0, 1.0);
    }

}
