package at.tuwien.aic2014.gr3.shared;

import twitter4j.Status;

public interface TwitterStatusProcessor {

    public void process(Status twitterStatus);
}
