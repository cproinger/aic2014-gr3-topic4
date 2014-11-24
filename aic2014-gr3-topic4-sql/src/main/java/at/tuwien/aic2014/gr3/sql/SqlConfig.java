package at.tuwien.aic2014.gr3.sql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@ComponentScan(basePackages = "at.tuwien.aic2014.gr3.sql")
@PropertySource("classpath:/META-INF/topic4/sql.properties")
public class SqlConfig {

	@Value("${db.url}")
	private String url;
	
	@Value("${db.user}")
	private String user;
	
	@Value("${db.pwd}")
	private String pwd;
	
	@Value("${db.driverClass}")
	private String driverClass;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
	@Bean
	public Connection connection() throws SQLException {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pwd);
		ds.setDriverClassName(driverClass);
		return ds.getConnection();
	}
}
