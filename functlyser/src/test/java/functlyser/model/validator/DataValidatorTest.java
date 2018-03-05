package functlyser.model.validator;

import functlyser.model.Data;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.Errors;

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
    public void testValidate_WhenProfileIdIsNull() {
        Data target = getPerfectData();
        target.setProfileId(null);

        sut.validate(target, errors);

        Mockito.verify(errors).rejectValue(anyString(), anyString(), anyString());
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

    @Test
    public void testValidate_WhenColumnNameIsEmpty() {
        Data target = getPerfectData();
        target.getColumns().put(" ", 5.0);

        sut.validate(target, errors);

        Mockito.verify(errors).rejectValue(anyString(), anyString(), anyString());
    }

    private Data getPerfectData() {
        Data data = new Data();
        data.setProfileId(new ObjectId("5a9c2061c529401e74584c5f"));
        data.setFileName("duplex.csv");
        data.setColumns(new HashMap<>());
        data.getColumns().put("col1", 100.0);
        data.getColumns().put("col2", 100.0);
        return data;
    }
}
