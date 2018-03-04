package functlyser.Controller;

import functlyser.model.Profile;
import functlyser.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/profile/list", method = RequestMethod.GET)
    public Page<Profile> list(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        return profileService.list(pageNum, pageSize);
    }
}
