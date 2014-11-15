package at.tuwien.aic2014.gr3.tweetsminer.filters;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Tokenizer + name finder + stopwords removal
 */
public class SmartTokenizerTweetFilter implements TweetFilter<String[],String> {

    private static final String INVALID_WORD_REG_EXP = "^[^\\w]+";

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
            else if (!stopwords.contains(tokens[currentTokenIndex].toLowerCase()) &&
                    !tokens[currentTokenIndex].matches(INVALID_WORD_REG_EXP)) {
                smartTokens.add(tokens[currentTokenIndex]);
                currentTokenIndex++;
            }
            else {
                currentTokenIndex++;
            }
        }

        nameFinder.clearAdaptiveData();

        String[] preparedSmartTokens = new String[smartTokens.size()];
        smartTokens.toArray(preparedSmartTokens);
        return new DataCarrier<>(preparedSmartTokens);
    }

    private String buildName(String[] tokens, Span nameMatch) {
        String name = tokens[nameMatch.getStart()];

        for (int i = nameMatch.getStart() + 1; i < nameMatch.getEnd(); ++i) {
            if (tokens[i].matches(INVALID_WORD_REG_EXP)) {
                name += tokens[i];
            }
            else {
                name += String.format(" %s", tokens[i]);
            }
        }

        return name;
    }

}
