package at.tuwien.aic2014.integraiontests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import at.tuwien.aic2014.gr3.domain.UserAndCount;
import at.tuwien.aic2014.gr3.shared.AnalysisRepository;
import at.tuwien.aic2014.web.RootConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootConfig.class)
public class AnalysisRepoIT {

	@Autowired
	private AnalysisRepository repo;
	
	@Test
	public void test() {
		Iterable<UserAndCount> usrs = repo.findMostRetweetedUsers();
		for(UserAndCount uac : usrs) {
			System.out.println(uac);
		}
	}
}
