package at.tuwien.aic2014.gr3.tweetsminer.filters;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import java.util.*;

/**
 * Tokenizer + name finder + stopwords removal
 */
public class SmartTokenizerTweetFilter implements TweetFilter<String[],String> {

    private static final String INVALID_WORD_REG_EXP = "^\\W+";

    private static final String TOKEN_STRIP_REG_EXP = "^\\W+|\\W+$";

    private static final String VALID_TOPIC_REG_EXP = "^[a-zA-Z0-9]+$";

    private Tokenizer tokenizer;
    private NameFinderME nameFinder;
    private Set<String> stopwords;

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void setNameFinder(NameFinderME nameFinder) {
        this.nameFinder = nameFinder;
    }

    public void setStopwords(String[] stopwords) {
        this.stopwords = new HashSet<>(Arrays.asList(stopwords));
    }

    @Override
    public DataCarrier<String[]> filter(DataCarrier<String> carrier) {
        String text = carrier.getData();

        String[] tokens = tokenizer.tokenize(text);

        Span[] nameMatches = nameFinder.find(tokens);

        List<String> smartTokens = new ArrayList<>();
        int currentNameMatchIndex = 0;
        int currentTokenIndex = 0;

        while (currentTokenIndex < tokens.length) {
            if (currentNameMatchIndex < nameMatches.length &&
                    nameMatches[currentNameMatchIndex].contains(currentTokenIndex)) {
                smartTokens.add (buildName (tokens, nameMatches[currentNameMatchIndex]));
                currentTokenIndex += nameMatches[currentNameMatchIndex].length();
                currentNameMatchIndex++;
            }
            else {
                String strippedToken = strip(tokens[currentTokenIndex]);
                if (isValidTopic (strippedToken)) {
                    smartTokens.add(strippedToken);
                    currentTokenIndex++;
                }
                else {
                    currentTokenIndex++;
                }
            }
        }

        nameFinder.clearAdaptiveData();

        String[] preparedSmartTokens = new String[smartTokens.size()];
        smartTokens.toArray(preparedSmartTokens);
        return new DataCarrier<>(preparedSmartTokens);
    }

    private boolean isValidTopic(String strippedToken) {
        return strippedToken.length() > 3 &&
                !stopwords.contains(strippedToken) &&
                strippedToken.matches(VALID_TOPIC_REG_EXP);
    }

    private String buildName(String[] tokens, Span nameMatch) {
        String name = tokens[nameMatch.getStart()];

        for (int i = nameMatch.getStart() + 1; i < nameMatch.getEnd(); ++i) {
            if (tokens[i].matches(INVALID_WORD_REG_EXP)) {
                name += tokens[i];  //Punctuation needs no space, such as: J. Brown
            }
            else {
                name += String.format(" %s", tokens[i]);
            }
        }

        return name;
    }

    private String strip (String token) {
        return token.replaceAll(TOKEN_STRIP_REG_EXP, "").trim().toLowerCase();
    }
}
