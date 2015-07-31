package com.vgarshyn.twittasteroid.contentprovider.tweet;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.vgarshyn.twittasteroid.contentprovider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code tweet} table.
 *
 * Created by v.garshyn on 26.07.15.
 */
public class TweetContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return TweetColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where           The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable TweetSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where           The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable TweetSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public TweetContentValues putTweetId(long value) {
        mContentValues.put(TweetColumns.TWEET_ID, value);
        return this;
    }

    public TweetContentValues putCreatedAt(String value) {
        mContentValues.put(TweetColumns.CREATED_AT, value);
        return this;
    }

    public TweetContentValues putOriginalJson(String value) {
        mContentValues.put(TweetColumns.ORIGINAL_JSON, value);
        return this;
    }

}
