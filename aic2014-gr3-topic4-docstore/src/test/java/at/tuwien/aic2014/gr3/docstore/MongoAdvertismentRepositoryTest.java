package at.tuwien.aic2014.gr3.docstore;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.UserTopic;
import com.mongodb.DB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: putzchri
 * Date: 19.01.15
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocStoreConfig.class)
public class MongoAdvertismentRepositoryTest  {

    @Autowired
    private DB db;

    @Before
    public void clearCollection() {
        if ("aicdocstore-test".equals(db.getName())) {
            db.getCollection("ads").drop();
        }
    }

    @Test
    public void testInterestsOfSingleUser(){
        Map<String,Object> map = new HashMap();
        map.put("to","car");
        map.put("cnt",10);
        UserTopic interest = new UserTopic(map);
        map.put("to","bmw");
        map.put("cnt",5);
        UserTopic interest1 = new UserTopic(map);
        map.put("to","girls");
        map.put("cnt",100);
        UserTopic interest2 = new UserTopic(map);

        List<UserTopic> interests = new ArrayList<>();
        interests.add(interest);
        interests.add(interest1);
        interests.add(interest2);
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DocStoreConfig.class);
        MongoAdvertismentRepository mongoAdvertisment = ctx.getBean(MongoAdvertismentRepository.class);
        Path path = Paths.get("aic2014-gr3-topic4-docstore\\src\\test\\resources\\img");
        mongoAdvertisment.save(path);
        Collection<Advertisment> list = mongoAdvertisment.findByInterests(interests, 5);
        if (list.isEmpty()){
            Assert.fail("List should not be null!");
        }
    }

}
