package at.tuwien.aic2014.web.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.spring.VaadinUI;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TweetRepository;
import at.tuwien.aic2014.web.controller.GUIController;
import at.tuwien.aic2014.web.controller.TestDataController;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
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
	
	GUIController controller = new TestDataController();
	
	private static final String UID ="UID";
	private static final String NAME = "Name";
	private static final String SCREENNAME = "Screenname";
	private static final String LOCATION = "Location";
	private static final String FOLLOWERCOUNT = "Follower-Count";
	private static final String FRIENDSCOUNT = "Friends-Count";
	private static final String LISTEDCOUNT = "Listed-Count";
	private static final String FAVOURITESCOUNT = "Favourites-Count";
	
	
	private TextField user_search = new TextField("User(id or name): ");
	private OptionGroup interests_mode =new OptionGroup("Based on interests: ");
	private ListSelect interests = new ListSelect("Interests:");
	private Panel listAdPanel = new Panel("Advertisement");
	private GridLayout adGrid = new GridLayout(3,1);
	
	
	@Autowired
	private transient TweetRepository tweetRepo;

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
	TextField table_search = new TextField("Search:");
	Table table = createUserGrid();
	formLayout_userPanel.addComponents(table_search,table);

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
			user = controller.getUserById(Long.parseLong(user_search.getValue()));
			}catch(NumberFormatException e){
				user = controller.getUserByName(user_search.getValue());
			}
			if(user == null){
				System.out.println("Fehler");
				user_search.setComponentError(new UserError("user not exist!"));
			}else{
				adGrid.removeAllComponents();
				interests.removeAllItems();
				List<String> user_interests;
				List<String> ads;
				if(interests_mode.getValue().toString().equals("existing")){
					System.out.println("Existing");
					user_interests = controller.getExistingInterestsForUser(user.getId());
					ads = controller.getAdsForUserExistingInterests(user.getId());
				}else{
					System.out.println("Potetial");
					user_interests = controller.getPotetialInterestsForUser(user.getId());
					ads = controller.getAdsForUserPotentiaPotentialInterests(user.getId());
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
		table.addContainerProperty(UID, Long.class, -1);
		table.addContainerProperty(NAME, String.class, "-");
		table.addContainerProperty(SCREENNAME, String.class, "-");
		table.addContainerProperty(LOCATION, String.class, "-");
		table.addContainerProperty(FOLLOWERCOUNT, Integer.class, 0);
		table.addContainerProperty(FRIENDSCOUNT, Integer.class, 0);
		table.addContainerProperty(LISTEDCOUNT, Integer.class, 0);
		table.addContainerProperty(FAVOURITESCOUNT, Integer.class, 0);
		
		
		List<TwitterUser> users = controller.getAllUser();
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
	return table;
	}
	
}
