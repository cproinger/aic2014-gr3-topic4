package at.tuwien.aic2014.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import at.tuwien.aic2014.gr3.docstore.DocStoreConfig;
import at.tuwien.aic2014.gr3.suggest.SuggestConfig;

@Configuration
@Import({
	SuggestConfig.class, 
	DocStoreConfig.class})//TODO import other module-configs. 
@ImportResource(value = "classpath*:/tweetsMinerTestContext.xml")
public class RootConfig {
	
}
