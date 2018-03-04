package functlyser.model.validator;

import functlyser.model.Profile;
import functlyser.model.ProfileInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;

public class ProfileValidatorTest {

    private Errors errors;
    private ProfileValidator sut;

    @Before
    public void before() {
        sut = new ProfileValidator();
        errors = Mockito.mock(Errors.class);
    }

    @Test
    public void test_supports_shouldReturnTrue_whenProfile() {
        boolean result = sut.supports(Profile.class);

        assertTrue(result);
    }

    @Test
    public void test_validate() {
        Profile profile = getPerfectProfile();

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenProfileNameIsEmpty() {
        Profile profile = getPerfectProfile();
        profile.setName("");

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenProfileNameIsWhiteSpace() {
        Profile profile = getPerfectProfile();
        profile.setName("  ");

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenProfileNameIsMoreThan60Chars() {
        Profile profile = getPerfectProfile();
        profile.setName(String.join("", Collections.nCopies(61, "a")));

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenColumnsIsNull() {
        Profile profile = getPerfectProfile();
        profile.setColumns(null);

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenColumnsHasLessThan2() {
        Profile profile = getPerfectProfile();
        profile.setColumns(new HashMap<String, ProfileInfo>() {{
            put("col1", new ProfileInfo());
        }});

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenColumnHasBlankKey() {
        Profile profile = getPerfectProfile();
        profile.setColumns(new HashMap<String, ProfileInfo>() {{
            put(" ", new ProfileInfo());
            put("col1", new ProfileInfo());
        }});

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenColumnIndexIsNotInOrder() {
        Profile profile = getPerfectProfile();
        profile.setColumns(new HashMap<String, ProfileInfo>() {{
            put("col1", new ProfileInfo());
            put("col2", new ProfileInfo() {{
                setIndex(5);
            }});
        }});

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void test_validate_whenOutputColumnNameDoesnotMatch() {
        Profile profile = getPerfectProfile();
        profile.setOutputColumn("1234");

        sut.validate(profile, errors);

        Mockito.verify(errors, Mockito.atLeastOnce()).rejectValue(anyString(), anyString(), anyString());
    }

    private Profile getPerfectProfile() {
        Profile profile = new Profile();
        profile.setName("test");
        profile.setOutputColumn("col1");
        profile.setColumns(new HashMap<String, ProfileInfo>());
        profile.getColumns().put("col1", new ProfileInfo());
        profile.getColumns().put("col2", new ProfileInfo());
        profile.getColumns().get("col2").setIndex(1);
        profile.getColumns().put("col3", new ProfileInfo());
        profile.getColumns().get("col3").setIndex(2);
        return profile;
    }
}
