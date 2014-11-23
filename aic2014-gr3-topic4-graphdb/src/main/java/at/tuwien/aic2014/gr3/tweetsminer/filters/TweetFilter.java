package at.tuwien.aic2014.gr3.tweetsminer.filters;

public interface TweetFilter<O,I> {

    public DataCarrier<O> filter (DataCarrier<I> carrier);
}
