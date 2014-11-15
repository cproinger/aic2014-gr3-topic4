package at.tuwien.aic2014.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import at.tuwien.aic2014.gr3.docstore.DocStoreConfig;

@Configuration
@Import({DocStoreConfig.class})//TODO import other module-configs. 
public class RootConfig {

}
