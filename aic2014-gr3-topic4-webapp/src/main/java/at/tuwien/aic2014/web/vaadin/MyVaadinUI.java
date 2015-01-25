package at.tuwien.aic2014.web.vaadin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;









import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.spring.VaadinUI;










import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.PotentialInterest;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.UserTopic;
import at.tuwien.aic2014.gr3.shared.TweetRepository;
import at.tuwien.aic2014.web.controller.GUIController;
import at.tuwien.aic2014.web.controller.Neo4jDataController;
import at.tuwien.aic2014.web.controller.TestDataController;










import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

@Title("AIC Topic 4")
@VaadinUI
@Theme("runo")
@Configurable(preConstruction = true, dependencyCheck = true)
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	GUIController guiController;
	
//	private final static Logger LOG = LoggerFactory.getLogger(MyVaadinUI.class);

	private static final String UID ="UID";
	private static final String NAME = "Name";
	private static final String SCREENNAME = "Screenname";
	private static final String LOCATION = "Location";
	private static final String FOLLOWERCOUNT = "Follower-Count";
	private static final String FRIENDSCOUNT = "Friends-Count";
	private static final String LISTEDCOUNT = "Listed-Count";
	private static final String FAVOURITESCOUNT = "Favourites-Count";
	
	
	private TextField user_search = new TextField("User(id): ");
	private OptionGroup interests_mode =new OptionGroup("Based on interests: ");
	private ListSelect interests = new ListSelect("Interests:");
	private Panel listAdPanel = new Panel("Advertisement");
	private GridLayout adGrid = new GridLayout(3,1);
	Table table =createUserGrid();
	
	
//	@Autowired
//	private transient TweetRepository tweetRepo;

	@Override
	protected void init(VaadinRequest request) {
		TabSheet hl = new TabSheet();

	// Have it take all space available in the layout.
	hl.setSizeFull();


	// Some components to put in the Accordion.
	Panel userPanel = new Panel("User");
	Panel adPanel = new Panel("Advertising");
	
	//UserPanel	
	FormLayout formLayout_userPanel = new FormLayout();
	TextField processedCountMoreThan = new TextField("processedCountMoreThan:");
	TextField maxResults = new TextField("maxResults:");
	OptionGroup order =new OptionGroup("Order: ");
	order.addItems("ascending","descending");
	order.select("ascending");
	Button submit = new Button("Submit");
	submit.addClickListener(new Button.ClickListener() {
		@Override
		public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
			try{
				submit.setComponentError(null);
				int maxResultsInt = Integer.parseInt(maxResults.getValue());
				int processedCountMoreThanInt = Integer.parseInt(processedCountMoreThan.getValue());
				boolean asc =  order.getValue().toString().equals("ascending") ? true :false;
				updateTable(asc, processedCountMoreThanInt, maxResultsInt);
			}catch(Exception e){
				submit.setComponentError(new UserError("Fehler bei Abfrage!"));
				System.err.println(e);
				System.err.println(e.getStackTrace());
			}
		}
	});
	formLayout_userPanel.addComponents(processedCountMoreThan,maxResults,order,submit,table);

	//AdPanel
	FormLayout formLayout_adPanel = new FormLayout();
	Button user_search_button = new Button("search");
	interests_mode.addItems("existing","potential");
	interests_mode.select("existing");
	interests.setWidth(100, Unit.PIXELS);
	interests.setNullSelectionAllowed(false);
	listAdPanel.setContent(adGrid);

	user_search_button.addClickListener(new Button.ClickListener() {
		@Override
		public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
			TwitterUser user;
			user_search.setComponentError(null);
			try{
			user = guiController.getUserById(Long.parseLong(user_search.getValue()));
			
			user = new TwitterUser();
			user.setId(Long.parseLong(user_search.getValue()));
			}catch(NumberFormatException e){
				user = null;
				
			}
			if(user == null){
				System.out.println("Fehler");
				user_search.setComponentError(new UserError("user not exist!"));
			}else{
				adGrid.removeAllComponents();
				interests.removeAllItems();
				List<String> user_interests = new ArrayList<String>();
				List<String> ads  = new ArrayList<String>();
				if(interests_mode.getValue().toString().equals("existing")){
					System.out.println("Existing");
					for(UserTopic usertopic : guiController.getExistingInterestsForUser(user.getId())){
						user_interests.add(usertopic.getTopic() + " (" + usertopic.getCnt() + ")");
					}
					for(Advertisment ad : guiController.getAdsForUserExistingInterests(user.getId())){
						ads.add(ad.getTags().toString());
						//TODO pics
					}
				}else{
					System.out.println("Potetial");
					for(PotentialInterest pi : guiController.getPotetialInterestsForUser(user.getId())){
						user_interests.add(pi.getTopic()+ " (" + pi.getLen()+ ")");
					}
					for(Advertisment ad : guiController.getAdsForUserPotentiaPotentialInterests(user.getId())){
						ads.add(ad.getTags().toString());
						//TODO pics
					}
				}
				interests.addItems(user_interests.toArray());
				for(String ad : ads){
					adGrid.addComponent(new Label(ad));
					adGrid.addComponent(new Label("Pic-dump"));
					adGrid.addComponent(new Label("Interests-dump"));
					
				}
			}
			
		}
	});


	interests.setRows(5);
	
	formLayout_adPanel.addComponents(user_search,user_search_button,interests_mode,interests,listAdPanel);
	
	adPanel.setContent(formLayout_adPanel);
	userPanel.setContent(formLayout_userPanel);
	
	
	// Add the components as tabs in the Accordion.
	hl.addTab(userPanel);
	hl.addTab(adPanel);

	setContent(hl);
	}
	
	private Table createUserGrid(){
		Table table = new Table();
		StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter() {
		    protected java.text.NumberFormat getFormat(Locale locale) {
		        NumberFormat format = super.getFormat(locale);
		        format.setGroupingUsed(false);
		        return format;
		    };
		};
		// either set for the field or in your field factory for multiple fields
		table.addContainerProperty(UID, Long.class, -1);
		table.setConverter(UID, plainIntegerConverter);
		table.addContainerProperty(NAME, String.class, "-");
		table.addContainerProperty(SCREENNAME, String.class, "-");
		table.addContainerProperty(LOCATION, String.class, "-");
		table.addContainerProperty(FOLLOWERCOUNT, Integer.class, 0);
		table.addContainerProperty(FRIENDSCOUNT, Integer.class, 0);
		table.addContainerProperty(LISTEDCOUNT, Integer.class, 0);
		table.addContainerProperty(FAVOURITESCOUNT, Integer.class, 0);
		
		
////		List<TwitterUser> users = guiController.getAllUser();
//		List<TwitterUser> users = new ArrayList<TwitterUser>();
//		for(TwitterUser user: users){
//			Object newItemId = table.addItem();
//			Item row1 = table.getItem(newItemId);
//			row1.getItemProperty(UID).setValue(Long.valueOf(user.getId()));
//			row1.getItemProperty(NAME).setValue(user.getName());
//			row1.getItemProperty(SCREENNAME).setValue(user.getScreenName());
//			row1.getItemProperty(LOCATION).setValue(user.getLocation());
//			row1.getItemProperty(FOLLOWERCOUNT).setValue(user.getFollowersCount());
//			row1.getItemProperty(FRIENDSCOUNT).setValue(user.getFriendsCount());
//			row1.getItemProperty(LISTEDCOUNT).setValue(user.getListedCount());
//			row1.getItemProperty(FAVOURITESCOUNT).setValue(user.getFavouritesCount());
//
//		}
	return table;
	}
	
	private void updateTable(boolean ascending, int processedCountMoreThan, int maxResults){
		this.table.removeAllItems();
		List<TwitterUser> users = guiController.getInterstedUsers(ascending, processedCountMoreThan, maxResults);
		for(TwitterUser user: users){
			Object newItemId = table.addItem();
			Item row1 = table.getItem(newItemId);
			row1.getItemProperty(UID).setValue(Long.valueOf(user.getId()));
			row1.getItemProperty(NAME).setValue(user.getName());
			row1.getItemProperty(SCREENNAME).setValue(user.getScreenName());
			row1.getItemProperty(LOCATION).setValue(user.getLocation());
			row1.getItemProperty(FOLLOWERCOUNT).setValue(user.getFollowersCount());
			row1.getItemProperty(FRIENDSCOUNT).setValue(user.getFriendsCount());
			row1.getItemProperty(LISTEDCOUNT).setValue(user.getListedCount());
			row1.getItemProperty(FAVOURITESCOUNT).setValue(user.getFavouritesCount());
		}
	}
	
}
