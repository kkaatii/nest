package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.ArrowType;
import photon.tube.model.Owner;
import photon.tube.model.OwnerAndFrameMapper;
import photon.tube.query.Query;
import photon.tube.query.QueryBuilder;
import photon.tube.query.QueryResult;
import photon.tube.query.SectionConfig;
import photon.tube.service.QueryService;

@RestController
@RequestMapping("/api/oaf")
public class OwnerApiController {

    private final QueryService qs;
    private final OwnerAndFrameMapper oafMapper;

    @Autowired
    public OwnerApiController(QueryService qs, OwnerAndFrameMapper oafMapper) {
        this.qs = qs;
        this.oafMapper = oafMapper;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Owner user(@RequestParam String aid) {
        Owner o = oafMapper.selectByAuthId(aid);
        return o;
    }

}
