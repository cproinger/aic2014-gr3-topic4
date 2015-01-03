package at.tuwien.aic2014.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.vaadin.spring.EnableVaadin;
import org.vaadin.spring.VaadinUI;

@Configuration
@Import(RootConfig.class)
@ComponentScan(includeFilters = {@Filter(type = FilterType.ANNOTATION, value = Controller.class)

	, @Filter(type = FilterType.ANNOTATION, value = VaadinUI.class)})
//@ComponentScan(basePackageClasses = MyVaadinUI.class)
@EnableWebMvc
@EnableVaadin
@EnableSpringConfigured 
public class ServletConfig extends WebMvcConfigurerAdapter {

}
