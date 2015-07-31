package com.vgarshyn.twittasteroid.contentprovider.tweet;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.contentprovider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code tweet} table.
 *
 * Created by v.garshyn on 26.07.15.
 */
public class TweetCursor extends AbstractCursor implements TweetModel {
    public TweetCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(TweetColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code tweet_id} value.
     */
    public long getTweetId() {
        Long res = getLongOrNull(TweetColumns.TWEET_ID);
        if (res == null)
            throw new NullPointerException("The value of 'tweet_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code created_at} value.
     */
    @Nullable
    public String getCreatedAt() {
        String res = getStringOrNull(TweetColumns.CREATED_AT);
        return res;
    }

    /**
     * Get the {@code original_json} value.
     */
    @Nullable
    public String getOriginalJson() {
        String res = getStringOrNull(TweetColumns.ORIGINAL_JSON);
        return res;
    }

    public Tweet getTweet(Gson gson) {
        return gson.fromJson(getOriginalJson(), Tweet.class);
    }

}
