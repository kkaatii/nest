package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import photon.tube.auth.AccessLevel;
import photon.tube.model.Owner;
import photon.tube.auth.OafService;

import java.util.List;

@RestController
@RequestMapping("/api/oaf")
public class OafApiController {

    private final OafService oafService;

    @Autowired
    public OafApiController(OafService oafService) {
        this.oafService = oafService;
    }

    @RequestMapping(value = "/owner", method = RequestMethod.GET)
    public Owner owner(@RequestParam String aid) {
        return oafService.getOwnerByAuthId(aid);
    }

    @RequestMapping("/frames-readable")
    public List<String> readableFrames(@RequestParam Integer oid, @RequestParam String on) {
        return oafService.getAccessibleFrames(new Owner(oid, on), AccessLevel.READ);
    }

}
