package functlyser.service;

import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Profile;
import functlyser.model.ProfileInfo;
import functlyser.model.validator.ProfileValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.ProfileRepository;
import org.assertj.core.api.StringAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ProfileServiceTest extends BaseServiceTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Mock
    private ValidatorRunner<ProfileValidator> profileValidator;

    @Mock
    private Errors errors;

    private ProfileService sut;

    @Before
    public void before() {
        sut = new ProfileService(profileRepository, profileValidator);
    }

    @After
    public void after() {
        mongoOperations.dropCollection(Profile.class);
    }

    @Test
    public void testCreate_ShouldReturnProfileWithId() {
        Profile profile = getPerfectProfile();
        Mockito.when(profileValidator.validate(profile)).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(false);

        Profile result = sut.create(profile);

        assertFalse(result.getId().isEmpty());
        assertThat(mongoOperations.findAll(Profile.class).size(), is(1));
    }

    @Test(expected = ApiException.class)
    public void testCreate_ShouldThrowError_whenDuplicateName() {
        Profile profile = getPerfectProfile();
        mongoOperations.save(profile);
        Mockito.when(profileValidator.validate(profile)).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(false);

        Profile result = sut.create(profile);
    }

    @Test(expected = ValidationException.class)
    public void testCreate_ShouldThrowError_whenValidationFails() {
        Profile profile = getPerfectProfile();
        mongoOperations.save(profile);
        Mockito.when(profileValidator.validate(profile)).thenReturn(errors);
        Mockito.when(errors.hasErrors()).thenReturn(true);

        Profile result = sut.create(profile);
    }


    private Profile getPerfectProfile() {
        Profile profile = new Profile();
        profile.setName("test");
        return profile;
    }
}
