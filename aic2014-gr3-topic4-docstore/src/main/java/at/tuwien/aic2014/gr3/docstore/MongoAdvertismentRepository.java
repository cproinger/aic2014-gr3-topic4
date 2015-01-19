package at.tuwien.aic2014.gr3.docstore;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.IHasTopic;
import at.tuwien.aic2014.gr3.shared.AdvertismentRepository;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

@Repository
public class MongoAdvertismentRepository implements AdvertismentRepository {

    @Autowired
    private static DB db;

    @Override
    public Collection<Advertisment> findByInterests(List<? extends IHasTopic> interests,
                                                    int max) throws UnsupportedOperationException {
        ArrayList<Advertisment> list = new ArrayList<Advertisment>();

        while (list.size() < max) {

        }
        //TODO christoph
        //throw new UnsupportedOperationException("not yet implemented");
        return list;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DocStoreConfig.class);
        MongoAdvertismentRepository ad = ctx.getBean(MongoAdvertismentRepository.class);
        BasicDBList list = new BasicDBList();
        try {
            Files.walk(Paths.get("D:\\Banner")).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String path = filePath.toString().toLowerCase();
                    if(path.endsWith(".jpg")){
                        String[] tags = readFile(path);
                        for(String tag : tags){
                            list.add(tag);
                            System.out.println(tag);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        BasicDBObject jo = new BasicDBObject().append("tags", list);
        db.getCollection("ads").save(jo);
    }

    private static String[] readFile(String path) {
        String[] tags = null;
        try {
            FileInputStream is = new FileInputStream(path + ".txt");
            List<String> lines = IOUtils.readLines(is);
            for(String line : lines){
                tags =  line.split(",");
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
			IOUtils.copy(is, output);
			output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }

    private void save() {

    }
}
