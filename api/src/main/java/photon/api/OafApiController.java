package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.Owner;
import photon.tube.model.OwnerAndFrameMapper;
import photon.tube.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/oaf")
public class OafApiController {

    private final AuthService authService;
    private final OwnerAndFrameMapper oafMapper;

    @Autowired
    public OafApiController(AuthService authService, OwnerAndFrameMapper oafMapper) {
        this.authService = authService;
        this.oafMapper = oafMapper;
    }

    @RequestMapping(value = "/owner", method = RequestMethod.GET)
    public Owner owner(@RequestParam String aid) {
        return oafMapper.selectOwnerByAuthId(aid);
    }

    @RequestMapping("/readable-frames")
    public List<String> readableFrames(@RequestParam Integer oid) {
        return oafMapper.selectFramesAccessibleTo(oid, AuthService.READ_ACCESS);
    }

}
