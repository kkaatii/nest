package photon.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.action.ActionException;
import photon.tube.query.QueryCallback;
import photon.tube.model.Owner;
import photon.tube.query.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api/q")
public class QueryApiController {

    private final QueryService queryService;

    @Autowired
    public QueryApiController(QueryService queryService) {
        this.queryService = queryService;
    }

    @RequestMapping(value = "/search")
    public
    @ResponseBody
    Future<QueryResult> search(@RequestParam String query,
                               @RequestParam(name = "_oid") Integer ownerId,
                               @RequestParam(name = "_on") String ownerName) {
        CompletableFuture<QueryResult> result = new CompletableFuture<>();
        queryService.executeQuery(new Owner(ownerId, ownerName), query, new QueryCallback<QueryResult>() {
            @Override
            public void onSuccess(QueryResult input) {
                result.complete(input);
            }

            @Override
            public void onException(ActionException ae) {
                result.completeExceptionally(ae);
            }
        });
        return result;
    }
}
