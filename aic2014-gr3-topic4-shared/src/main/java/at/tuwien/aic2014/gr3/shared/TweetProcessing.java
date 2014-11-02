package at.tuwien.aic2014.gr3.shared;

public interface TweetProcessing {
    void safeTweetIntoSQL(String rawJSON);
}
