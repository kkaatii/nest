package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.ArrowType;
import photon.tube.model.OwnerAndFrameMapper;
import photon.tube.query.Query;
import photon.tube.query.QueryBuilder;
import photon.tube.query.QueryResult;
import photon.tube.query.SectionConfig;
import photon.tube.service.QueryService;

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
                             @RequestParam(defaultValue = "ANY") ArrowType arrowType,
                             @RequestParam(defaultValue = "false") boolean reversed,
                             @RequestParam(defaultValue = SectionConfig.DO_NOT_SECTION) String sectionMode,
                             @RequestParam(defaultValue = "0") int leftLimit,
                             @RequestParam(defaultValue = "-1") int rightLimit,
                             @RequestParam(defaultValue = "true") boolean leftInclusive,
                             @RequestParam(defaultValue = "false") boolean rightInclusive) {
        //if (qid.length == 0) return new QueryResult(null, GraphSlice.BLANK);
        Query query = new QueryBuilder()
                .ownerId(2)
                .type(graphView)
                .args(new Object[]{ ids, reversed ? arrowType.reverse() : arrowType })
                .sectionConfig(new SectionConfig(sectionMode, leftLimit, rightLimit, leftInclusive, rightInclusive))
                .build();
        return qs.resultOf(query);
    }
}
