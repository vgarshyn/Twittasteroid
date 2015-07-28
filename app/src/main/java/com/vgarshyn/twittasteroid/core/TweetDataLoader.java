package com.vgarshyn.twittasteroid.core;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetCursor;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetSelection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v.garshyn on 27.07.15.
 */
public class TweetDataLoader extends AsyncTaskLoader<List<Tweet>> {

    protected List<Tweet> mLastDataList = null;

    public TweetDataLoader(Context context) {
        super(context);
    }

    /**
     * Runs on a worker thread, loading in our data. Delegates the real work to
     * concrete subclass' buildCursor() method.
     */
    @Override
    public List<Tweet> loadInBackground() {
        List<Tweet> data = new ArrayList<Tweet>();
        TweetSelection selection = new TweetSelection().orderByTweetId(true);
        TweetCursor cursor = selection.query(getContext());
//        Cursor cursor1 = getContext().getContentResolver().query(TweetColumns.CONTENT_URI, null, null, null, null);
//        TweetCursor cursor = null;
//        if (cursor1 != null) {
//            cursor = new TweetCursor(cursor1);
//        }
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Gson gson = new Gson();
            do {
                data.add(cursor.getTweet(gson));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        return data;
    }

    /**
     * Runs on the UI thread, routing the results from the background thread to
     * whatever is using the dataList.
     */
    @Override
    public void deliverResult(List<Tweet> dataList) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            emptyDataList(dataList);
            return;
        }
        List<Tweet> oldDataList = mLastDataList;
        mLastDataList = dataList;
        if (isStarted()) {
            super.deliverResult(dataList);
        }
        if (oldDataList != null && oldDataList != dataList
                && oldDataList.size() > 0) {
            emptyDataList(oldDataList);
        }
    }

    /**
     * Starts an asynchronous load of the list data. When the result is ready
     * the callbacks will be called on the UI thread. If a previous load has
     * been completed and is still valid the result may be passed to the
     * callbacks immediately.
     * <p/>
     * Must be called from the UI thread.
     */
    @Override
    protected void onStartLoading() {
        if (mLastDataList != null) {
            deliverResult(mLastDataList);
        }
        if (takeContentChanged() || mLastDataList == null
                || mLastDataList.size() == 0) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread, triggered by a call to stopLoading().
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Must be called from the UI thread, triggered by a call to cancel(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    public void onCanceled(List<Tweet> dataList) {
        if (dataList != null && dataList.size() > 0) {
            emptyDataList(dataList);
        }
    }

    /**
     * Must be called from the UI thread, triggered by a call to reset(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        if (mLastDataList != null && mLastDataList.size() > 0) {
            emptyDataList(mLastDataList);
        }
        mLastDataList = null;
    }

    protected void emptyDataList(List<Tweet> dataList) {
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                dataList.remove(i);
            }
        }
    }
}
