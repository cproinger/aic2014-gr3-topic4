package at.tuwien.aic2014.gr3.tweetsminer.filters;

public class RemoveHashtagsTweetFilter implements TweetFilter<String, String> {

    private static final String HASHTAG_MENTIONS_REG_EXP = "^#\\w+[^\\w]|([^\\w])#\\w+";

    @Override
    public DataCarrier<String> filter(DataCarrier<String> carrier) {
        String text = carrier.getData();
        carrier.setData(text.replaceAll(HASHTAG_MENTIONS_REG_EXP, "$1"));

        return carrier;
    }
}
