package at.tuwien.aic2014.gr3.shared;

public interface RepositoryIterator<T> {

    public boolean hasNext() throws RepositoryException;

    public T next() throws RepositoryException;
}
