package at.tuwien.aic2014.gr3.docstore;


import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.IHasTopic;
import at.tuwien.aic2014.gr3.shared.AdvertismentRepository;
import com.mongodb.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class MongoAdvertismentRepository implements AdvertismentRepository {

    @Autowired
    private DB db;

    @Override
    public Collection<Advertisment> findByInterests(List<? extends IHasTopic> interests,
                                                    int max) throws UnsupportedOperationException {
        ArrayList<Advertisment> list = new ArrayList<Advertisment>();
        if (db.collectionExists("ads")) {
            DBCollection collection = db.getCollection("ads");
            DBCursor basic = collection.find();
            basic.next();
            while (basic.hasNext()) {
                DBObject obj = basic.curr();
                BasicDBList dblist = (BasicDBList) obj.get("tags");
                for (IHasTopic interest : interests) {
                    if (dblist.contains(interest.getTopic())) {
                        byte[] pic = (byte[]) obj.get("image");
                        String[] tags = new String[dblist.size()];
                        int i = 0;
                        for (Object tag : dblist) {
                            tags[i] = (String) tag;
                            i++;
                        }
                        if (list.size() < max) {
                            list.add(new Advertisment(pic, tags));
                        }
                        break;
                    }
                }
                basic.next();
            }
        }

        return list;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DocStoreConfig.class);
        MongoAdvertismentRepository ad = ctx.getBean(MongoAdvertismentRepository.class);
        ad.save(Paths.get("C:\\Banner"));
    }

    private static String[] readFile(String path) {
        String[] tags = null;
        try {
            FileInputStream is = new FileInputStream(path + ".txt");

            List<String> lines = IOUtils.readLines(is);
            for (String line : lines) {
                tags = line.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public void save(Path imgPath) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Files.walk(imgPath).forEach(filePath -> {
                BasicDBList list = new BasicDBList();
                if (Files.isRegularFile(filePath)) {
                    String path = filePath.toString().toLowerCase();
                    if (path.endsWith(".jpg")) {
                        String[] tags = readFile(filePath.toString());
                        if (tags != null) {
                            for (String tag : tags) {
                                list.add(tag.toLowerCase());
                            }
                        }
                        try {
                            FileInputStream is = new FileInputStream(filePath.toString());
                            IOUtils.copy(is, out);
                            byte[] data = out.toByteArray();
                            BasicDBObject image_jo = new BasicDBObject().append("image", data);
                            image_jo.append("tags", list);
                            db.getCollection("ads").save(image_jo);
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
