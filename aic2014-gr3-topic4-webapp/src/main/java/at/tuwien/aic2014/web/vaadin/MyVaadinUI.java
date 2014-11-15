package at.tuwien.aic2014.web.vaadin;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;

import at.tuwien.aic2014.gr3.shared.TweetRepository;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Title("AIC-POLYGLOT PERSISTENCE")
@VaadinUI
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = 1L;

	@Autowired
	private transient TweetRepository tweetRepo;
	
	private Label hello = new Label("Hello vaadin world!");
	private  Panel content = new Panel(hello);
	
	
	
	@Override
	protected void init(VaadinRequest request) {
		Component c = null;
		
		HorizontalSplitPanel comp = new HorizontalSplitPanel();
		VerticalLayout sideMenue = new VerticalLayout();
		Button btnTweetsPerUser = new Button("tweets per user");
		btnTweetsPerUser.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				content.setContent(tweetsPerUserPanel());
			}

		});
		sideMenue.addComponent(btnTweetsPerUser);
		
		comp.addComponent(sideMenue);
		comp.addComponent(content);
		setContent(comp);
	}

	private Component tweetsPerUserPanel() {
		Chart chart = new Chart(ChartType.BAR);
		
		return chart;
	}
}
