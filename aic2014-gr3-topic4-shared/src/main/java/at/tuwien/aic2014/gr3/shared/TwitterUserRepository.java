package at.tuwien.aic2014.gr3.shared;

import at.tuwien.aic2014.gr3.domain.TwitterUser;

import java.util.Iterator;

public interface TwitterUserRepository {

    public TwitterUser save(TwitterUser twitterUser) throws RepositoryException;

    public TwitterUser readById (long userId) throws RepositoryException;

    public RepositoryIterator<TwitterUser> readAll() throws RepositoryException;
}
