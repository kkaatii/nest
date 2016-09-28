package nest.api;

import nest.data.ArrowType;
import nest.query.QueryContext;
import nest.query.QueryResult;
import nest.query.SliceParam;
import nest.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static nest.query.SliceParam.DO_NOT_SLICE;

@RestController
@RequestMapping("/api/query")
public class QueryApiController {

    @Autowired
    private QueryService queryService;

    @RequestMapping(value = "/{graphView}/{id}", method = RequestMethod.GET)
    public QueryResult query(@PathVariable String graphView,
                             @PathVariable int[] id,
                             @RequestParam(defaultValue = "unspecified") ArrowType arrowType,
                             @RequestParam(required = false) String token,
                             @RequestParam(required = false) String qid,
                             @RequestParam(defaultValue = "false") boolean reversed,
                             @RequestParam(defaultValue = DO_NOT_SLICE) String sliceMode,
                             @RequestParam(defaultValue = "0") int leftLimit,
                             @RequestParam(defaultValue = "-1") int rightLimit,
                             @RequestParam(defaultValue = "true") boolean leftInclusive,
                             @RequestParam(defaultValue = "true") boolean rightInclusive) {
        //if (id.length == 0) return new QueryResult(null, GraphSlice.BLANK);
        QueryContext qc = new QueryContext();
        qc.token = token;
        qc.qid = qid;
        qc.args = new Object[]{ id, reversed ? arrowType.reverse() : arrowType };
        qc.processorName = graphView;
        qc.sliceParam = new SliceParam(sliceMode, leftLimit, rightLimit, leftInclusive, rightInclusive);
        return queryService.query(qc);
    }
}
