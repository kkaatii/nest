package photon;

import com.google.inject.*;
import photon.action.*;
import photon.auth.MockOafService;
import photon.auth.OafService;
import photon.bootstrap.Photon;
import photon.model.CrudService;
import photon.model.MockCrudService;
import photon.query.QueryService;
import photon.query.QueryServiceImpl;
import photon.query.QueryTest;
import photon.util.Utils;

public class Test {

    public static void main(String... args) throws Exception {
//        ActionTest actionTest = new ActionTest();
//        QueryTest queryTest = new QueryTest();
//        actionTest.piTest(400, 100000);
//        queryTest.parseNumberTest();
//        queryTest.queryTest();

        Module module = new AbstractModule() {
            @Override
            public void configure() {
                bind(OafService.class).to(MockOafService.class).in(Scopes.SINGLETON);
                bind(MockCrudService.class).in(Scopes.SINGLETON);
                bind(QueryService.class).to(QueryServiceImpl.class).in(Scopes.SINGLETON);
            }

            @Provides
            CrudService mockCrudService() {
                MockCrudService service = new MockCrudService();
                service.setAndInitTestSet("simple");
                return service;
            }
        };
        Photon photon = new Photon(module);
        photon.startServer(8080);
    }


    /**
     * Test Utils tools
     */
    void parseNumberTest() {
        System.out.println("1. should be parsable float: " + ((Utils.isParsable("1.") & Utils.FLAG_FLOAT) == Utils.FLAG_FLOAT ? "Success" : "Failure"));
        System.out.println("1.2 should be parsable float: " + (Utils.isParsable("1.2") == Utils.FLAG_FLOAT ? "Success" : "Failure"));
        System.out.println("-1 should be parsable int: " + (Utils.isParsable("-1") == Utils.FLAG_INT ? "Success" : "Failure"));
        System.out.println("+1. should be parsable float: " + (Utils.isParsable("+1.") == Utils.FLAG_FLOAT ? "Success" : "Failure"));
        System.out.println("+2.1. should NOT be parsable: " + (Utils.isParsable("+2.1.") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println("1ss should NOT be parsable: " + (Utils.isParsable("1ss") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println("+ should be NOT parsable: " + (Utils.isParsable("+") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println(".5 should be NOT parsable: " + (Utils.isParsable(".5") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println(Utils.isParsable(" 5"));
    }

    /**
     * Test resource file access
     */
    void resourceTest() {

        String resource = "xml/mybatis/mybatis-config.xml";
        System.out.println(getClass().getClassLoader());
//        try {
//            InputStream inputStream = Resources.getResourceAsStream(resource);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
