package at.tuwien.aic2014.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(includeFilters = {@Filter(type = FilterType.ANNOTATION, value = Controller.class)})
@EnableWebMvc
public class ServletConfig extends WebMvcConfigurerAdapter {

}
