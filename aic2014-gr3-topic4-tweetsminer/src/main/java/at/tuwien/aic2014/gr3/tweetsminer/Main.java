package at.tuwien.aic2014.gr3.tweetsminer;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    public static void main (String[] args) {
        log.info("-------- Twitter Statuses Miner App Starting --------");

        ApplicationContext appCtx = new ClassPathXmlApplicationContext(
                "tweetsMinerContext.xml");

        TwitterStreamingStatusesMiner tweetsMiner =
                (TwitterStreamingStatusesMiner) appCtx.getBean("tweetsMiner");

        log.debug("Registering shutdown handler");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run () {
                log.debug("Shutdown handler called!");
                if (tweetsMiner.isRunning()) {
                    tweetsMiner.shutdown();
                }
            }
        });

        Thread tweetsMinerThread = new Thread(tweetsMiner);
        tweetsMinerThread.start();
        do {
            try {
                tweetsMinerThread.join();
            } catch (InterruptedException e) {
                log.debug("Tweets Miner Thread got interrupted! Don't care, keep working!", e);
            }
        } while (tweetsMiner.isRunning());
    }
}
