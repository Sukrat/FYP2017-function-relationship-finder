package functlyser.Controller;

import functlyser.model.Profile;
import functlyser.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController extends Controller {

    private ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = "/profile/create", method = RequestMethod.POST)
    public Profile create(@RequestBody Profile profile) {
        return profileService.create(profile);
    }
}
