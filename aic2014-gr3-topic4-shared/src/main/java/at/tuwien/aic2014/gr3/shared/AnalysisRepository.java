package at.tuwien.aic2014.gr3.shared;

import java.util.List;

import at.tuwien.aic2014.gr3.domain.InterestedUsers;
import at.tuwien.aic2014.gr3.domain.UserAndCount;
import at.tuwien.aic2014.gr3.domain.UserTopic;

public interface AnalysisRepository {

	Iterable<UserAndCount> findMostRetweetedUsers();

	InterestedUsers findInterestedUsers();

	List<UserTopic> findExistingInterestsForUser(long userId);

	List<UserTopic> findPotentialInterestsForUser(long userId);
}
