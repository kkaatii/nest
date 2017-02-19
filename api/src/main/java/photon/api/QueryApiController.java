package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.Owner;
import photon.tube.query.QueryRequest;
import photon.tube.query.QueryResult;
import photon.tube.query.QueryService;
import photon.tube.query.SegmentSpec;

import static photon.tube.query.SegmentSpec.NONE;

@RestController
@RequestMapping("/api/q")
public class QueryApiController {

    private final QueryService qs;

    @Autowired
    public QueryApiController(QueryService qs) {
        this.qs = qs;
    }

    @RequestMapping(value = "/pattern/{ids}", method = RequestMethod.GET)
    public QueryResult patternQuery(@PathVariable Integer[] ids,
                                    @RequestParam(name = "oid") Integer ownerId,
                                    @RequestParam(name = "on") String ownerName,
                                    @RequestParam(name = "p") String[] pattern,
                                    @RequestParam(name = "mode", defaultValue = NONE) String segmentMode,
                                    @RequestParam(name = "ll", defaultValue = "0") int leftLimit,
                                    @RequestParam(name = "rl", defaultValue = "-1") int rightLimit,
                                    @RequestParam(name = "li", defaultValue = "false") boolean leftInclusive,
                                    @RequestParam(name = "ri", defaultValue = "true") boolean rightInclusive) {
        QueryRequest queryRequest = new QueryRequest.Builder()
                .owner(new Owner(ownerId, ownerName))
                .handler("pattern")
                .args(new Object[]{ids, pattern})
                .segmentSpec(new SegmentSpec(segmentMode, leftLimit, rightLimit, leftInclusive, rightInclusive))
                .build();
        return qs.createQuery(queryRequest).result();
    }

    @RequestMapping(value = "/search")
    public QueryResult search(@RequestParam String searchString) {
        return qs.createQuery(searchString).result();
    }
}
