package at.tuwien.aic2014.gr3.docstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.DB;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.UserTopic;
import at.tuwien.aic2014.gr3.shared.AdvertismentRepository;

@Repository
public class MongoAdvertismentRepository implements AdvertismentRepository {

	@Autowired
	private DB db;
	
	@Override
	public Collection<Advertisment> findByInterests(List<UserTopic> interests,
			int max) {
		//TODO christoph
		//throw new UnsupportedOperationException("not yet implemented");
		return new ArrayList<Advertisment>();
	}

}
