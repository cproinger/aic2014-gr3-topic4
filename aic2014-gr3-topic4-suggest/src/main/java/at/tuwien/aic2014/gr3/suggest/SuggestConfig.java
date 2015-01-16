package at.tuwien.aic2014.gr3.suggest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;

/**
 * No configuration is imported here because the Suggest-module
 * is independent of the other spring-modules. 
 * 
 * @author cproinger
 *
 */
@Configuration
@ComponentScan(basePackageClasses = SuggestConfig.class)
@ImportResource(value = "classpath*:/tweetsMinerContext.xml")
@Lazy(true)
public class SuggestConfig {

}
