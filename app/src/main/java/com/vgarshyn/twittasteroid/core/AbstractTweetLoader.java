package com.vgarshyn.twittasteroid.core;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetCursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper Loader to dispatch and represent cursor into  tweet collection.
 * Encapsulate boilerplate loader logic
 *
 * Created by v.garshyn on 29.07.2015.
 */
public abstract class AbstractTweetLoader extends AsyncTaskLoader<List<Tweet>> {

    protected List<Tweet> mLastDataList = null;

    public AbstractTweetLoader(Context context) {
        super(context);
    }

    protected abstract TweetCursor queryCursor();

    /**
     * Runs on a worker thread, loading in our data. Delegates the real work to
     * concrete subclass queryCursor() method.
     *
     * @return
     */
    @Override
    public List<Tweet> loadInBackground() {
        List<Tweet> data = new ArrayList<Tweet>();
        TweetCursor cursor = queryCursor();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Gson gson = new Gson();
            do {
                data.add(cursor.getTweet(gson));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Runs on the UI thread, routing the results from the background thread to
     * whatever is using the data collection.
     *
     * @param dataList
     */
    @Override
    public void deliverResult(List<Tweet> dataList) {
        if (isReset()) {
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
     * Starts an asynchronous load of the list data.
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
        cancelLoad();
    }

    /**
     * Must be called from the UI thread, triggered by a call to cancel(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     * @param dataList
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
