package photon.bootstrap;

import org.mybatis.guice.XMLMyBatisModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MybatisModule extends XMLMyBatisModule {
    @Override
    protected void initialize() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream("mybatis.properties");
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        setClassPathResource("xml/mybatis/mybatis-config.xml");
        addProperties(prop);
    }
}
