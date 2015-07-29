package com.vgarshyn.twittasteroid.core;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetColumns;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetContentValues;

import java.util.List;

public class TweetIntentService extends IntentService {
    public static final int REQUEST_TWEET_COUNT = 50;
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_REFRESH = "twittasteroid.action.REFRESH";
    public static final String ACTION_REFRESH_COMPLETED = "twittasteroid.action.CANCEL_REFRESH";
    public static final String ACTION_LOAD_MORE = "twittasteroid.action.LOAD_MORE";
    public static final String ACTION_LOAD_MORE_COMPLETED = "twittasteroid.action.LOAD_MORE_COMPLETED";
    public static final String EXTRA_ERROR = "twittasteroid.extra.PARAM_ERROR";
    public static final String EXTRA_MAX_ID = "twittasteroid.extra.PARAM_MAX_ID";
    private static final String TAG = TweetIntentService.class.getSimpleName();
    private Gson gson = new Gson();

    public TweetIntentService() {
        super("TweetIntentService");
    }

    public static void startActionRefresh(Context context) {
        Intent intent = new Intent(context, TweetIntentService.class);
        intent.setAction(ACTION_REFRESH);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionLoadMore(Context context, Long maxid) {
        Intent intent = new Intent(context, TweetIntentService.class);
        intent.setAction(ACTION_LOAD_MORE);
        if (maxid != null) {
            intent.putExtra(EXTRA_MAX_ID, maxid);
        }
//        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                handleActionRefresh();
            } else if (ACTION_LOAD_MORE.equals(action)) {
                Long maxId = null;
                if (intent.hasExtra(EXTRA_MAX_ID)) {
                    maxId = intent.getLongExtra(EXTRA_MAX_ID, 0);
                }
                handleActionLoadMore(maxId);
            }
        }
    }

    private void handleActionRefresh() {
        StatusesService service = Twitter.getInstance().getApiClient().getStatusesService();
        service.homeTimeline(REQUEST_TWEET_COUNT, null, null, null, null, null, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        List<Tweet> data = result.data;
                        storeData(data);
                        refreshCompleted(null);
                    }

                    @Override
                    public void failure(TwitterException error) {
                        refreshCompleted(error.getLocalizedMessage());
                    }
                }
        );
    }

    private void storeData(List<Tweet> data) {
        ContentValues[] bulkDataset = new ContentValues[data.size()];
        int index = 0;
        for (Tweet tweet : data) {
            TweetContentValues tweetContentValues = new TweetContentValues()
                    .putTweetId(tweet.getId())
                    .putCreatedAt(tweet.createdAt)
                    .putOriginalJson(gson.toJson(tweet));
            bulkDataset[index] = tweetContentValues.values();
            index++;
        }
        getContentResolver().bulkInsert(TweetColumns.CONTENT_URI, bulkDataset);
    }

    private void refreshCompleted(String errorMessage) {
        Intent intent = new Intent(ACTION_REFRESH_COMPLETED);
        if (!TextUtils.isEmpty(errorMessage)) {
            intent.putExtra(EXTRA_ERROR, errorMessage);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleActionLoadMore(final Long paramMaxId) {
        StatusesService service = Twitter.getInstance().getApiClient().getStatusesService();
        service.homeTimeline(REQUEST_TWEET_COUNT, null, paramMaxId, null, null, null, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        List<Tweet> data = result.data;
                        storeData(data);
                        loadMoreCompleted(paramMaxId);
                    }

                    @Override
                    public void failure(TwitterException error) {
                        loadMoreCompleted(error.getLocalizedMessage());
                    }
                }
        );
    }

    private void loadMoreCompleted(Long maxId) {
        Intent intent = new Intent(ACTION_LOAD_MORE_COMPLETED);
        intent.putExtra(EXTRA_MAX_ID, maxId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void loadMoreCompleted(String errorMessage) {
        Intent intent = new Intent(ACTION_LOAD_MORE_COMPLETED);
        if (!TextUtils.isEmpty(errorMessage)) {
            intent.putExtra(EXTRA_ERROR, errorMessage);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
