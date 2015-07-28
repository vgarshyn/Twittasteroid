package com.vgarshyn.twittasteroid.contentprovider.tweet;

import android.support.annotation.Nullable;

/**
 * Data model for the {@code tweet} table.
 */
public interface TweetModel {

    /**
     * Get the {@code tweet_id} value.
     */
    long getTweetId();

    /**
     * Get the {@code created_at} value.
     * Can be {@code null}.
     */
    @Nullable
    String getCreatedAt();

    /**
     * Get the {@code original_json} value.
     * Can be {@code null}.
     */
    @Nullable
    String getOriginalJson();
}
