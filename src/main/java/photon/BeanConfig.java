package photon;

import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("photon.data.persistence")
public class BeanConfig {
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		String resource = "xml/mybatis/mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
        return new SqlSessionFactoryBuilder().build(inputStream);
	}
	
	@Bean
	public SqlSessionTemplate sessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}

}
