package functlyser.service;

import functlyser.Faker;
import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Profile;
import functlyser.model.validator.ProfileValidator;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.ProfileRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.Errors;

import java.util.List;

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
        super.before();
        sut = new ProfileService(profileRepository, profileValidator);
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

    @Test
    public void testList_ShouldReturnPageProfile() {
        long totalElements = 20;
        int pageSize = 5;
        for (int i = 0; i < totalElements; i++) {
            Profile profile = new Profile();
            profile.setName(Faker.nextString(30) + i);
            mongoOperations.save(profile);
        }

        Page<Profile> result = sut.list(3, pageSize);

        assertThat(result.getTotalElements(), is(totalElements));
        assertThat(result.getTotalPages(), is(4));
        assertThat(result.getContent().size(), is(pageSize));
    }

    @Test(expected = ApiException.class)
    public void testList_ShouldThrowError_whenPageNumLessThan0() {
        long totalElements = 20;
        int pageSize = 5;
        for (int i = 0; i < totalElements; i++) {
            Profile profile = new Profile();
            profile.setName(Faker.nextString(30) + i);
            mongoOperations.save(profile);
        }

        Page<Profile> result = sut.list(-1, pageSize);
    }

    @Test(expected = ApiException.class)
    public void testList_ShouldThrowError_whenPageSizeLessThan1() {
        long totalElements = 20;
        int pageSize = 5;
        for (int i = 0; i < totalElements; i++) {
            Profile profile = new Profile();
            profile.setName(Faker.nextString(30) + i);
            mongoOperations.save(profile);
        }

        Page<Profile> result = sut.list(0, 0);
    }

    private Profile getPerfectProfile() {
        Profile profile = new Profile();
        profile.setName("test");
        return profile;
    }
}
