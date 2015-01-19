package at.tuwien.aic2014.gr3.suggest;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.PotentialInterest;
import at.tuwien.aic2014.gr3.domain.UserTopic;
import at.tuwien.aic2014.gr3.shared.AdvertismentRepository;
import at.tuwien.aic2014.gr3.shared.AnalysisRepository;

@Service
public class AdSuggestionService {

	private final static Logger LOG = LoggerFactory.getLogger(AdSuggestionService.class);
	
	@Autowired
	private AnalysisRepository analysisRepository;
	
	@Autowired
	private AdvertismentRepository advertismentRepository;
	
	public Collection<Advertisment> suggestAdsForExistingUserInterests(long userId) {
		List<UserTopic> interests = analysisRepository.findExistingInterestsForUser(userId);
		LOG.info("existing interests for user {}: {}", userId, interests);
		return advertismentRepository.findByInterests(
				interests, 5);
	}
	
	public Collection<Advertisment> suggestAdsForPotentialUserInterests(long userId, int minLen, int maxLen) {
		List<PotentialInterest> interests = analysisRepository.findPotentialInterestsForUser(userId, minLen, maxLen);
		LOG.info("potential interests for user {}: {}", userId, interests);
		return advertismentRepository.findByInterests(
				interests, 5);
	}
}
