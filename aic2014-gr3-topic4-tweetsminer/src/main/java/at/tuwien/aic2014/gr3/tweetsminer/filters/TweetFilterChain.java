package at.tuwien.aic2014.gr3.tweetsminer.filters;

import java.util.List;

public class TweetFilterChain<O,I> implements TweetFilter<O,I> {

    List<TweetFilter> tweetFiltersChain;

    public TweetFilterChain(List<TweetFilter> tweetFiltersChain) {
        this.tweetFiltersChain = tweetFiltersChain;
    }

    @Override
    public DataCarrier<O> filter(DataCarrier<I> carrier) {
        DataCarrier processedDataCarrier = carrier;

        for (TweetFilter filter : tweetFiltersChain) {
            filter.getClass().getGenericSuperclass().getTypeName();
            processedDataCarrier = filter.filter(processedDataCarrier);
        }

        return processedDataCarrier;
    }
}
