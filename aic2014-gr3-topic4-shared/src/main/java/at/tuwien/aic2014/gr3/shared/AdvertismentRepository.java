package at.tuwien.aic2014.gr3.shared;

import java.util.Collection;
import java.util.List;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.IHasTopic;

public interface AdvertismentRepository {

	/**
	 * @param interests
	 * @param max
	 * 		at most how many Advertisments should be returned. 
	 * @return
	 * 		a collection of advertisments favouring the topics
	 *		with the highest counts. 
	 */
	public Collection<Advertisment> findByInterests(
			List<? extends IHasTopic> interests,
			int max);
}
