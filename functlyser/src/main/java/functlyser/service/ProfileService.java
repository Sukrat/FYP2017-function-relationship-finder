package functlyser.service;

import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import functlyser.model.Profile;
import functlyser.model.validator.ValidatorRunner;
import functlyser.repository.ProfileRepository;
import functlyser.model.validator.ProfileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static java.lang.String.format;

@Component
public class ProfileService extends Service {

    private ProfileRepository profileRepository;
    private ValidatorRunner<ProfileValidator> profileValidator;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, ValidatorRunner<ProfileValidator> profileValidator) {
        this.profileRepository = profileRepository;
        this.profileValidator = profileValidator;
    }

    public Profile create(Profile profile) {
        Errors errors = profileValidator.validate(profile);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        if (profileRepository.existsByName(profile.getName())) {
            throw new ApiException(format("Profile name '%s' already exists", profile.getName()));
        }
        Profile insert = profileRepository.insert(profile);
        return insert;
    }

    public Page<Profile> list(int pageNum, int pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        return profileRepository.findAll(pageable);
    }
}
