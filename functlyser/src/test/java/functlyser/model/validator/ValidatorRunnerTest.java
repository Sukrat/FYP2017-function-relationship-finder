package functlyser.model.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ValidatorRunnerTest {

    private Validator validator;
    private ValidatorRunner<Validator> sut;

    @Before
    public void before() {
        validator = Mockito.mock(Validator.class);
        sut = new ValidatorRunner<Validator>(validator);
    }

    @Test
    public void test_validate() {
        Object target = new Object();
        Mockito.when(validator.supports(target.getClass())).thenReturn(true);

        Errors errors = sut.validate(target);

        Mockito.verify(validator).validate(Mockito.eq(target), Mockito.any(BeanPropertyBindingResult.class));
    }
}
