package photon.api;

import photon.data.ArrowType;
import photon.query.Query;
import photon.query.QueryBuilder;
import photon.query.QueryResult;
import photon.query.SliceConfig;
import photon.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static photon.query.SliceConfig.DO_NOT_SLICE;

@RestController
@RequestMapping("/api/q")
public class QueryApiController {

    private QueryService queryService;

    @Autowired
    public QueryApiController(QueryService queryService) {
        this.queryService = queryService;
    }

    @RequestMapping(value = "/{graphView}/{id}", method = RequestMethod.GET)
    public QueryResult query(@PathVariable String graphView,
                             @PathVariable int[] id,
                             @RequestParam(defaultValue = "unspecified") ArrowType arrowType,
                             @RequestParam(defaultValue = "false") boolean reversed,
                             @RequestParam(defaultValue = DO_NOT_SLICE) String sliceMode,
                             @RequestParam(defaultValue = "0") int leftLimit,
                             @RequestParam(defaultValue = "-1") int rightLimit,
                             @RequestParam(defaultValue = "true") boolean leftInclusive,
                             @RequestParam(defaultValue = "true") boolean rightInclusive) {
        //if (qid.length == 0) return new QueryResult(null, GraphSlice.BLANK);
        Query query = new QueryBuilder()
                .type(graphView)
                .args(new Object[]{ id, reversed ? arrowType.reverse() : arrowType })
                .sliceConfig(new SliceConfig(sliceMode, leftLimit, rightLimit, leftInclusive, rightInclusive))
                .createQuery();
        return queryService.execute(query);
    }
}
