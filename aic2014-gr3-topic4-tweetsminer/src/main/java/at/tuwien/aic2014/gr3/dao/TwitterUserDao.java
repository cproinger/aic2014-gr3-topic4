package at.tuwien.aic2014.gr3.dao;

import at.tuwien.aic2014.gr3.domain.TwitterUser;

public interface TwitterUserDao {

    public TwitterUser create (TwitterUser twitterUser);

    public TwitterUser readById (long id);

    public void update (TwitterUser twitterUser);
}
