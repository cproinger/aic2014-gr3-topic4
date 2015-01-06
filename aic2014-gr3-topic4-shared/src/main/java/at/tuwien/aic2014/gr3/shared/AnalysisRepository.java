package at.tuwien.aic2014.gr3.shared;

import at.tuwien.aic2014.gr3.domain.UserRetweetedCount;

public interface AnalysisRepository {

	Iterable<UserRetweetedCount> findMostRetweetedUsers();
}
