package at.tuwien.aic2014.web.vaadin;

import org.springframework.web.context.WebApplicationContext;
import org.vaadin.spring.servlet.SpringAwareUIProvider;

import com.vaadin.server.UIProvider;

public class MyUIProvider extends SpringAwareUIProvider {

	public MyUIProvider(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
		// TODO Auto-generated constructor stub
	}

}
