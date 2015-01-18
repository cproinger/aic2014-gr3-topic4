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
    private Advertisment car;
    private Advertisment youtube;


    @Override
    public Collection<Advertisment> findByInterests(List<UserTopic> interests,
                                                    int max) throws UnsupportedOperationException {
        ArrayList<Advertisment> list = new ArrayList<Advertisment>();
        initAdvertisments();


        while (list.size() < max) {
            for (int i = 0; car.getTags().size() < i; i++) {
                if (interests.contains(car.getSingleTag(i))){
                    list.add(car);
                }
            }
        }
        //TODO christoph
        //throw new UnsupportedOperationException("not yet implemented");
        return list;
    }

    private void initAdvertisments() {
        car = new Advertisment("./ressource/CarBanner.jpg", Arrays.asList(new String[]{"car", "auto", "bmw", "audi", "vehicle"}));
        youtube = new Advertisment("", Arrays.asList(new String[]{"video", "videos", "youtube"}));

    }

}
