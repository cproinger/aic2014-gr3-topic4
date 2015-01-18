package at.tuwien.aic2014.gr3.docstore;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.UserTopic;
import at.tuwien.aic2014.gr3.shared.AdvertismentRepository;
import com.mongodb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Repository
public class MongoAdvertismentRepository implements AdvertismentRepository {

    @Autowired
    private DB db;

    @Override
    public Collection<Advertisment> findByInterests(List<UserTopic> interests,
                                                    int max) throws UnsupportedOperationException {
        ArrayList<Advertisment> list = new ArrayList<Advertisment>();
        Advertisment car = new Advertisment("", Arrays.asList(new String[]{"car", "auto", "bmw", "audi", "vehicle"}));

        while (list.size() < max) {
            if (interests.contains("car")) {
                list.add(car);
            }
        }
        //TODO christoph
        //throw new UnsupportedOperationException("not yet implemented");
        return list;
    }

}
