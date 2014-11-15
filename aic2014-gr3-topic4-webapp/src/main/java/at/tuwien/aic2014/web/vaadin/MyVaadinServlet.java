package at.tuwien.aic2014.web.vaadin;

import javax.servlet.annotation.WebServlet;

import org.vaadin.spring.servlet.SpringAwareVaadinServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet(urlPatterns = "/*")
@VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class)
public class MyVaadinServlet extends SpringAwareVaadinServlet {

	
}
