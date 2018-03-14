package functlyser.model.validator;

import functlyser.Faker;
import functlyser.model.Data;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;

public class DataValidatorTest {

    private Errors errors;
    private DataValidator sut;

    @Before
    public void before() {
        sut = new DataValidator();
        errors = Mockito.mock(Errors.class);
    }

    @Test
    public void testSupport() {
        boolean results = sut.supports(Data.class);

        assertTrue(results);
    }

    @Test
    public void testValidate() {
        Data target = getPerfectData();

        sut.validate(target, errors);

        Mockito.verify(errors, Mockito.never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void testValidate_WhenFileNameIsEmpty() {
        Data target = getPerfectData();
        target.setFileName("  ");

        sut.validate(target, errors);

        Mockito.verify(errors).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void testValidate_WhenColumnIsNull() {
        Data target = getPerfectData();
        target.setColumns(null);

        sut.validate(target, errors);

        Mockito.verify(errors).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void testValidate_WhenColumnIsEmpty() {
        Data target = getPerfectData();
        target.setColumns(new HashMap<>());

        sut.validate(target, errors);

        Mockito.verify(errors).rejectValue(anyString(), anyString(), anyString());
    }

    private Data getPerfectData() {
        Data data = new Data();
        data.setFileName("duplex.csv");
        data.setColumns(new HashMap<String, Double>() {{
            put("col0", Faker.nextDouble());
            put("col1", Faker.nextDouble());
        }});
        return data;
    }
}
