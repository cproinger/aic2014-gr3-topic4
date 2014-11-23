package at.tuwien.aic2014.gr3.tweetsminer.filters;

import java.util.List;

public class TweetFilterChain<O,I> implements TweetFilter<O,I> {

    private List<TweetFilter> tweetFiltersChain;

    public TweetFilterChain(List<TweetFilter> tweetFiltersChain) {
        this.tweetFiltersChain = tweetFiltersChain;
    }

    @Override
    public DataCarrier<O> filter(DataCarrier<I> carrier) {
        DataCarrier processedDataCarrier = carrier;

        for (TweetFilter filter : tweetFiltersChain) {
            //Requires the chain filters input - output to match
            //Eg: [I] -> F1 -> [type2] -> F2 -> [O]
            processedDataCarrier = filter.filter(processedDataCarrier);
        }

        return processedDataCarrier;
    }
}
