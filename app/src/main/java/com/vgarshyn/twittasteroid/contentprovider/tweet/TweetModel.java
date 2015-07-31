package com.vgarshyn.twittasteroid.contentprovider.tweet;


/**
 * Data model for the {@code tweet} table.
 *
 * Created by v.garshyn on 26.07.15.
 */
public interface TweetModel {

    /**
     * Get the {@code tweet_id} value.
     */
    long getTweetId();

    /**
     * Get the {@code created_at} value.
     */
    String getCreatedAt();

    /**
     * Get the {@code original_json} value.
     */
    String getOriginalJson();
}
