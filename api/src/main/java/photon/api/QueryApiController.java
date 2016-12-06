package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.ArrowType;
import photon.tube.model.Owner;
import photon.tube.query.QueryContext;
import photon.tube.query.QueryResult;
import photon.tube.query.SectionConfig;
import photon.tube.service.QueryService;

import static photon.tube.query.SectionConfig.DO_NOT_SECTION;

@RestController
@RequestMapping("/api/q")
public class QueryApiController {

    private final QueryService qs;

    @Autowired
    public QueryApiController(QueryService qs) {
        this.qs = qs;
    }

    // TODO change query controller
    @RequestMapping(value = "/{graphView}/{ids}", method = RequestMethod.GET)
    public QueryResult query(@PathVariable String graphView,
                             @PathVariable Integer[] ids,
                             @RequestParam(name="oid") Integer ownerId,
                             @RequestParam(name="on") String ownerName,
                             @RequestParam(name = "at", defaultValue = "ANY") ArrowType arrowType,
                             @RequestParam(name = "r", defaultValue = "false") boolean reversed,
                             @RequestParam(name = "mode", defaultValue = DO_NOT_SECTION) String sectionMode,
                             @RequestParam(name = "ll", defaultValue = "0") int leftLimit,
                             @RequestParam(name = "rl", defaultValue = "-1") int rightLimit,
                             @RequestParam(name = "li", defaultValue = "true") boolean leftInclusive,
                             @RequestParam(name = "ri", defaultValue = "false") boolean rightInclusive) {
        QueryContext context = new QueryContext.Builder()
                .owner(new Owner(ownerId, ownerName))
                .type(graphView)
                .args(new Object[]{ids, reversed ? arrowType.reverse() : arrowType})
                .sectionConfig(new SectionConfig(sectionMode, leftLimit, rightLimit, leftInclusive, rightInclusive))
                .build();
        return qs.createQuery(context).result();
    }
}
