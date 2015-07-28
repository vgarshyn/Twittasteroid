package com.vgarshyn.twittasteroid.contentprovider.tweet;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.vgarshyn.twittasteroid.contentprovider.base.AbstractSelection;

/**
 * Selection for the {@code tweet} table.
 */
public class TweetSelection extends AbstractSelection<TweetSelection> {
    @Override
    protected Uri baseUri() {
        return TweetColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection      A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code TweetCursor} object, which is positioned before the first entry, or null.
     */
    public TweetCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new TweetCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public TweetCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context    The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code TweetCursor} object, which is positioned before the first entry, or null.
     */
    public TweetCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new TweetCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public TweetCursor query(Context context) {
        return query(context, null);
    }


    public TweetSelection id(long... value) {
        addEquals("tweet." + TweetColumns._ID, toObjectArray(value));
        return this;
    }

    public TweetSelection idNot(long... value) {
        addNotEquals("tweet." + TweetColumns._ID, toObjectArray(value));
        return this;
    }

    public TweetSelection orderById(boolean desc) {
        orderBy("tweet." + TweetColumns._ID, desc);
        return this;
    }

    public TweetSelection orderById() {
        return orderById(false);
    }

    public TweetSelection tweetId(long... value) {
        addEquals(TweetColumns.TWEET_ID, toObjectArray(value));
        return this;
    }

    public TweetSelection tweetIdNot(long... value) {
        addNotEquals(TweetColumns.TWEET_ID, toObjectArray(value));
        return this;
    }

    public TweetSelection tweetIdGt(long value) {
        addGreaterThan(TweetColumns.TWEET_ID, value);
        return this;
    }

    public TweetSelection tweetIdGtEq(long value) {
        addGreaterThanOrEquals(TweetColumns.TWEET_ID, value);
        return this;
    }

    public TweetSelection tweetIdLt(long value) {
        addLessThan(TweetColumns.TWEET_ID, value);
        return this;
    }

    public TweetSelection tweetIdLtEq(long value) {
        addLessThanOrEquals(TweetColumns.TWEET_ID, value);
        return this;
    }

    public TweetSelection orderByTweetId(boolean desc) {
        orderBy(TweetColumns.TWEET_ID, desc);
        return this;
    }

    public TweetSelection orderByTweetId() {
        orderBy(TweetColumns.TWEET_ID, false);
        return this;
    }

    public TweetSelection createdAt(String... value) {
        addEquals(TweetColumns.CREATED_AT, value);
        return this;
    }

    public TweetSelection createdAtNot(String... value) {
        addNotEquals(TweetColumns.CREATED_AT, value);
        return this;
    }

    public TweetSelection createdAtLike(String... value) {
        addLike(TweetColumns.CREATED_AT, value);
        return this;
    }

    public TweetSelection createdAtContains(String... value) {
        addContains(TweetColumns.CREATED_AT, value);
        return this;
    }

    public TweetSelection createdAtStartsWith(String... value) {
        addStartsWith(TweetColumns.CREATED_AT, value);
        return this;
    }

    public TweetSelection createdAtEndsWith(String... value) {
        addEndsWith(TweetColumns.CREATED_AT, value);
        return this;
    }

    public TweetSelection orderByCreatedAt(boolean desc) {
        orderBy(TweetColumns.CREATED_AT, desc);
        return this;
    }

    public TweetSelection orderByCreatedAt() {
        orderBy(TweetColumns.CREATED_AT, false);
        return this;
    }

    public TweetSelection originalJson(String... value) {
        addEquals(TweetColumns.ORIGINAL_JSON, value);
        return this;
    }

    public TweetSelection originalJsonNot(String... value) {
        addNotEquals(TweetColumns.ORIGINAL_JSON, value);
        return this;
    }

    public TweetSelection originalJsonLike(String... value) {
        addLike(TweetColumns.ORIGINAL_JSON, value);
        return this;
    }

    public TweetSelection originalJsonContains(String... value) {
        addContains(TweetColumns.ORIGINAL_JSON, value);
        return this;
    }

    public TweetSelection originalJsonStartsWith(String... value) {
        addStartsWith(TweetColumns.ORIGINAL_JSON, value);
        return this;
    }

    public TweetSelection originalJsonEndsWith(String... value) {
        addEndsWith(TweetColumns.ORIGINAL_JSON, value);
        return this;
    }

    public TweetSelection orderByOriginalJson(boolean desc) {
        orderBy(TweetColumns.ORIGINAL_JSON, desc);
        return this;
    }

    public TweetSelection orderByOriginalJson() {
        orderBy(TweetColumns.ORIGINAL_JSON, false);
        return this;
    }
}
