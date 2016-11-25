package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.Owner;
import photon.tube.model.OwnerAndFrameMapper;
import photon.tube.service.AuthService;

import java.util.List;
import java.util.stream.Collectors;

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

    @RequestMapping("/frames-readable")
    public List<String> readableFrames(@RequestParam Integer oid, @RequestParam String on) {
        List<String> results = oafMapper.selectFramesAccessibleTo(oid, AuthService.READ_ACCESS);
        return (results == null) ? null : results.stream().map(frame -> {
            String[] s = frame.split("@");
            return s[1].equals(on) ? s[0] : frame;
        }).collect(Collectors.toList());
    }

}
