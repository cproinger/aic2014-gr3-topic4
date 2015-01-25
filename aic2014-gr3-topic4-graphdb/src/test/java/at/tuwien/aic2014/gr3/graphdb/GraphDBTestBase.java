package at.tuwien.aic2014.gr3.graphdb;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
@ActiveProfiles(profiles = {"activate-mocks"})
public abstract class GraphDBTestBase {

	@Rule
	@Autowired
	public MockExternalComponents mockExternalComponentsRule;
}
