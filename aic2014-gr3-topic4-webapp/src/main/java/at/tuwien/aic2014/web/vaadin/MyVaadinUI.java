package at.tuwien.aic2014.web.vaadin;

import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.spring.VaadinUI;

import at.tuwien.aic2014.gr3.shared.TweetRepository;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Title("AIC-POLYGLOT PERSISTENCE")
@VaadinUI
@Configurable(preConstruction = true, dependencyCheck = true)
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = 1L;

	@Autowired
	private transient TweetRepository tweetRepo;
	
	private Label hello = new Label("Hello vaadin world!");
	private  Panel content = new Panel(hello);
	
	
	
	@Override
	protected void init(VaadinRequest request) {
		System.out.println("tweetRepo: " + tweetRepo);
		Component c = null;
		
		HorizontalLayout comp = new HorizontalLayout();
		comp.setSizeFull();
		VerticalLayout sideMenue = new VerticalLayout();
		Button btnTweetsPerUser = new Button("tweets per user");
//		btnTweetsPerUser.addClickListener(new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				content.setContent(tweetsPerUserPanel());
//			}
//
//		});
		sideMenue.addComponent(btnTweetsPerUser);
		
		comp.addComponent(sideMenue);
		content.setContent(tweetsPerUserPanel());
		comp.addComponent(content);
		content.setSizeFull();
		setContent(comp);
	}

	private Component tweetsPerUserPanel() {
		DataSeries dataSeries = new DataSeries().add(2, 6, 7, 10);
		
		SeriesDefaults seriesDefaults = new SeriesDefaults()
		.setRenderer(SeriesRenderers.BAR);
		Axes axes = new Axes()
			.addAxis(new XYaxis()
				.setRenderer(AxisRenderers.CATEGORY)
				.setTicks(new Ticks().add("a", "b", "c", "d")));
		Highlighter highlighter = new Highlighter().setShow(false);
		
		Options options = new Options()
			.setSeriesDefaults(seriesDefaults)
			.setAxes(axes)
			.setHighlighter(highlighter);
		DCharts chart = new DCharts().setDataSeries(dataSeries)
									.setOptions(options);
		chart.setCaption("my chart");
		chart.setHeight("300px");
		chart.setWidth("300px");
		chart.show();
		
		return chart;
	}
}
