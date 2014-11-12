package at.tuwien.aic2014.gr3.tweetsminer.filters;

public class RemoveUrlsTweetFilter implements TweetFilter<String,String> {

    private static final String URL_REG_EXP = "\\s*http://[^\\s]*\\s*";

    @Override
    public DataCarrier<String> filter(DataCarrier<String> carrier) {
        String text = carrier.getData();
        carrier.setData(text.replaceAll(URL_REG_EXP, " "));

        return carrier;
    }
}
