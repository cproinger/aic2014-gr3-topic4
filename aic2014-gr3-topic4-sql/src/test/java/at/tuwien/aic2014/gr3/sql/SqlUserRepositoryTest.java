package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.RepositoryIterator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:sqlTestContext.xml")
public class SqlUserRepositoryTest {

    private static final String DROP_ALL_QUERY =
            "DROP ALL OBJECTS DELETE FILES";

    @Autowired
    private SqlUserRepository userRepo;

    @Autowired
    private DataSource dataSource;

    @After
    public void tearDown() throws Exception {
        try(Connection connection = dataSource.getConnection();
                PreparedStatement stat = connection.prepareStatement(DROP_ALL_QUERY)) {
            stat.executeUpdate();
        }

        userRepo.initSQL();
    }

    @Test
    public void testSaveNewUser() throws Exception {
        TwitterUser testTwitterUser = new TwitterUser();
        testTwitterUser.setId(1);

        userRepo.save(testTwitterUser);

        TwitterUser persistedTwitterUser = userRepo.readById(testTwitterUser.getId());

        assertEquals(testTwitterUser, persistedTwitterUser);
    }

    @Test
    public void testReadAllHugeDataSet() throws Exception {
        int size = 30000;
        setUpHugeDataSet(size);
        doReadAllAsserts(size);
    }

    @Test
    public void testReadAllConcurrentAccess() throws Exception {
        int nUsers = 3000;
        setUpHugeDataSet(nUsers);

        int nThreads = 10;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < nThreads; ++i) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        doReadAllAsserts(nUsers);
                    } catch (Exception e) {
                        fail("Unexpected exception! " + e);
                    }
                }
            };
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private void doReadAllAsserts(int size) throws RepositoryException {
        RepositoryIterator<TwitterUser> it = userRepo.readAll();

        for (int i = 0; i < size; ++i) {
            assertTrue(it.hasNext());
            assertEquals(i, it.next().getId());
            //Thread.sleep(1000); //simulate work -> test db timeouts
        }

        assertFalse(it.hasNext());
    }

    private void setUpHugeDataSet(int size) throws RepositoryException {
        for (int i = 0; i < size; ++i) {
            TwitterUser user = new TwitterUser();
            user.setId(i);

            userRepo.save(user);
        }
    }
}
