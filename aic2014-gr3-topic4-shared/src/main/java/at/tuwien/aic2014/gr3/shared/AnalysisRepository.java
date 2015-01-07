package at.tuwien.aic2014.gr3.shared;

import at.tuwien.aic2014.gr3.domain.InterestedUsers;
import at.tuwien.aic2014.gr3.domain.UserAndCount;

public interface AnalysisRepository {

	Iterable<UserAndCount> findMostRetweetedUsers();

	InterestedUsers findInterestedUsers();
}
