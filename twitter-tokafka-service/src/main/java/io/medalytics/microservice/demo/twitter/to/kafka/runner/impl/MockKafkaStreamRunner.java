package io.medalytics.microservice.demo.twitter.to.kafka.runner.impl;

import exception.TwitterToKafkaServiceException;
import io.medalytics.microservice.demo.twitter.to.kafka.TwitterToKafkaServiceConfigData;
import io.medalytics.microservice.demo.twitter.to.kafka.listener.TwitterKafkaStatusListener;
import io.medalytics.microservice.demo.twitter.to.kafka.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;
    private TwitterStream twitterStream;

    private static final Random RANDOM = new Random();
    private static final String[] WORDS =
            new String[]{"is simply dummy text of the printing and typesetting industry. " +
                    "Lorem Ipsum has been the industry's standard dummy text ever since " +
                    "the 1500s, when an unknown printer took a galley of type and scrambled " +
                    "it to make a type specimen book. It has survived not only five centuries, " +
                    "but also the leap into electronic typesetting, remaining essentially unchanged. " +
                    "It was popularised in the 1960s with the release of Letraset sheets containing " +
                    "Lorem Ipsum passages, and more recently with desktop publishing software like Aldus " +
                    "PageMaker including versions of Lorem Ipsum.".split(" ")};

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";


    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterKafkaStatusListener twitterKafkaStatusListener) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException {
        String[] keyWords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {});
        int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
        int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
        long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
        LOG.info("Started mock filtering twitter stream for keywords {}", Arrays.toString(keyWords));
        simulateTwitterStream(keyWords, minTweetLength, maxTweetLength, sleepTimeMs);
    }

    private void simulateTwitterStream(String[] keyWords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
        Executors.newSingleThreadExecutor()
                .submit(() -> {
                    try {
                        while (true) {
                            String formattedTweetAsJson = getFormattedTweet(keyWords, minTweetLength, maxTweetLength);
                            Status status = TwitterObjectFactory.createStatus(formattedTweetAsJson);
                            twitterKafkaStatusListener.onStatus(status);
                            sleep(sleepTimeMs);
                        }
                    } catch (TwitterException ex) {
                        LOG.error("Error creating twitter status");
                    }

            }
        );

    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException ex) {
            throw new TwitterToKafkaServiceException("");
        }
    }

    private String getFormattedTweet(String[] keyWords, int minTweetLength, int maxTweetLength) {
        String[] params = new String[] {
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keyWords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return formatTweetAsJson(params);
    }

    private String formatTweetAsJson(String[] params) {
        String tweet = tweetAsRawJson;

        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keyWords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        return constructRandomTweet(tweet, tweetLength);
    }

    private String constructRandomTweet(StringBuilder tweet, int tweetLength) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
        }
        return tweet.toString().trim();
    }
}
