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

        RepositoryIterator<TwitterUser> it = userRepo.readAll();

        for (int i = 0; i < size; ++i) {
            assertTrue(it.hasNext());
            assertEquals(i, it.next().getId());
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
