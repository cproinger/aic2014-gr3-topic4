package at.tuwien.aic2014.gr3.tweetsminer.filters;

public class RemoveRetweetHeaderTweetFilter implements TweetFilter<String,String> {

    private static final String RT_REG_EXP = "^RT @[^:]+: ";

    @Override
    public DataCarrier<String> filter(DataCarrier<String> carrier) {
        String text = carrier.getData();
        carrier.setData(text.replaceFirst(RT_REG_EXP, ""));

        return carrier;
    }
}
