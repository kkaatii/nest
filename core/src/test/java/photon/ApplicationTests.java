package photon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;
import photon.tube.action.ActionException;
import photon.tube.auth.MockOafService;
import photon.tube.auth.OafService;
import photon.tube.model.CrudService;
import photon.tube.model.MockCrudService;
import photon.tube.model.Owner;
import photon.tube.query.QueryCallback;
import photon.tube.query.QueryResult;
import photon.tube.query.QueryService;

// TODO NOT working, understand why???
@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

    @Bean
    @Primary
    public CrudService crudService() {
        MockCrudService mockCrudService = new MockCrudService();
        mockCrudService.setAndInitTestSet("simple");
        return mockCrudService;
    }

    @Bean
    @Primary
    public OafService oafService() {
        return new MockOafService();
    }

    @Autowired
    private QueryService queryService;

    @Test
    public void contextLoads() {
        System.out.println("---------------- Start test  ----------------");
        String query = "{\"actions\":[{\"action\":\"search\",\"arguments\":{\"searcher\":\"Chain\", \"origins\":[0], \"arrow_type\":\"parent_of\"}}]}";
        queryService.executeQuery(new Owner(0, ""), query, new QueryCallback<QueryResult>() {
            @Override
            public void onSuccess(QueryResult input) {
                System.out.println(input.getSegment());
            }

            @Override
            public void onException(ActionException ae) {
                ae.printStackTrace();
            }
        });
    }

}
