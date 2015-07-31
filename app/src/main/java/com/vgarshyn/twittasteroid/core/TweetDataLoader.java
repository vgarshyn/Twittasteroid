package com.vgarshyn.twittasteroid.core;

import android.content.Context;

import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetCursor;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetSelection;

/**
 * Loader to represent cursor given from content provider into
 * tweet collection
 *
 * Created by v.garshyn on 27.07.15.
 */
public class TweetDataLoader extends AbstractTweetLoader {

    private long mSinceId;

    public TweetDataLoader(Context context) {
        this(context, 0);
    }

    public TweetDataLoader(Context context, long sinceId) {
        super(context);
        mSinceId = sinceId;
    }

    @Override
    protected TweetCursor queryCursor() {
        TweetSelection selection = new TweetSelection().orderByTweetId(true);
        if (mSinceId > 0) {
            selection.tweetIdLt(mSinceId);
        }
        selection.limit(50);
        TweetCursor cursor = selection.query(getContext());
        return cursor;
    }
}
