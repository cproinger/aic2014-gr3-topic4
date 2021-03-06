package at.tuwien.aic2014.gr3.shared;

import java.util.List;

import at.tuwien.aic2014.gr3.domain.InterestedUsers;
import at.tuwien.aic2014.gr3.domain.InterestedUsersResult;
import at.tuwien.aic2014.gr3.domain.PotentialInterest;
import at.tuwien.aic2014.gr3.domain.UserAndCount;
import at.tuwien.aic2014.gr3.domain.UserTopic;

public interface AnalysisRepository {

	Iterable<UserAndCount> findMostRetweetedUsers();

	@Deprecated
	InterestedUsers findInterestedUsers();

	List<UserTopic> findExistingInterestsForUser(long userId);

	List<PotentialInterest> findPotentialInterestsForUser(long userId, int minLen, int maxLen);

	List<InterestedUsersResult> findInterstedUsers(boolean ascending,
			int processedCountMoreThan, int maxResults);
}
