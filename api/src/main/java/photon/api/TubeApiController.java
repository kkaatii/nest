package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.ArrowType;
import photon.tube.query.Query;
import photon.tube.query.QueryBuilder;
import photon.tube.query.QueryResult;
import photon.tube.query.SectionConfig;
import photon.tube.service.QueryService;

import static photon.tube.query.SectionConfig.DO_NOT_SECTION;

@RestController
@RequestMapping("/api/q")
public class TubeApiController {

    private final QueryService qs;

    @Autowired
    public TubeApiController(QueryService qs) {
        this.qs = qs;
    }

    @RequestMapping(value = "/{graphView}/{ids}", method = RequestMethod.GET)
    public QueryResult query(@PathVariable String graphView,
                             @PathVariable Integer[] ids,
                             @RequestParam(defaultValue = "UNSPECIFIED") ArrowType arrowType,
                             @RequestParam(defaultValue = "false") boolean reversed,
                             @RequestParam(defaultValue = DO_NOT_SECTION) String sectionMode,
                             @RequestParam(defaultValue = "0") int leftLimit,
                             @RequestParam(defaultValue = "-1") int rightLimit,
                             @RequestParam(defaultValue = "true") boolean leftInclusive,
                             @RequestParam(defaultValue = "false") boolean rightInclusive) {
        //if (qid.length == 0) return new QueryResult(null, GraphSlice.BLANK);
        Query query = new QueryBuilder()
                .type(graphView)
                .args(new Object[]{ ids, reversed ? arrowType.reverse() : arrowType })
                .sectionConfig(new SectionConfig(sectionMode, leftLimit, rightLimit, leftInclusive, rightInclusive))
                .build();
        return qs.resultOf(query);
    }
}
