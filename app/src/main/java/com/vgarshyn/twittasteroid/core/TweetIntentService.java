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
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetCursor;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetSelection;

import java.util.List;

/**
 * Service to dispatch requests to twitter api and
 * store data into ContentProvider
 * <p/>
 * Created by v.garshyn on 26.07.15.
 */
public class TweetIntentService extends IntentService {
    public static final int REQUEST_TWEET_COUNT = 50;
    public static final String ACTION_REFRESH = "twittasteroid.action.REFRESH";
    public static final String ACTION_REFRESH_COMPLETED = "twittasteroid.action.CANCEL_REFRESH";
    public static final String ACTION_REFRESH_COMPLETED_WITOUT_UPDATE = "twittasteroid.action.CANCEL_REFRESH_COMPLETED_WITHOUT_UPDATE";
    public static final String ACTION_LOAD_MORE = "twittasteroid.action.LOAD_MORE";
    public static final String ACTION_LOAD_MORE_COMPLETED = "twittasteroid.action.LOAD_MORE_COMPLETED";
    public static final String ACTION_TRUNCATE = "twittasteroid.action.TRUNCATE";
    public static final String ACTION_FORCE_REFRESH = "twittasteroid.action.FORCE_REFRESH";
    public static final String EXTRA_ERROR = "twittasteroid.extra.PARAM_ERROR";
    public static final String EXTRA_MAX_ID = "twittasteroid.extra.PARAM_MAX_ID";
    private static final String TAG = TweetIntentService.class.getSimpleName();
    private Gson gson = new Gson();

    public TweetIntentService() {
        super("TweetIntentService");
    }

    /**
     * Starts this service to perform action REFRESH.
     *
     */
    public static void startActionRefresh(Context context) {
        Intent intent = new Intent(context, TweetIntentService.class);
        intent.setAction(ACTION_REFRESH);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action LOAD_MORE with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     */
    public static void startActionLoadMore(Context context, Long maxid) {
        Intent intent = new Intent(context, TweetIntentService.class);
        intent.setAction(ACTION_LOAD_MORE);
        if (maxid != null) {
            intent.putExtra(EXTRA_MAX_ID, maxid);
        }
        context.startService(intent);
    }

    /**
     * Start remove old stored data and load new.
     *
     * @param context
     */
    public static void startActionTruncate(Context context) {
        Intent intent = new Intent(context, TweetIntentService.class);
        intent.setAction(ACTION_TRUNCATE);
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
            } else if (ACTION_TRUNCATE.equals(action)) {
                handleActionTruncate();
            }
        }
    }

    private void handleActionTruncate() {
        TweetSelection selection = new TweetSelection().orderById();
        selection.delete(getContentResolver());
        notifyForceRefresh();
    }

    private void handleActionRefresh() {
        StatusesService service = Twitter.getInstance().getApiClient().getStatusesService();
        service.homeTimeline(REQUEST_TWEET_COUNT, null, null, null, null, null, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        List<Tweet> data = result.data;
                        if (isNeedUpdateData(data)) {
                            storeData(data);
                            refreshCompleted(null);
                        } else {
                            refreshCompletedWithoutUpdate();
                        }
                    }

                    @Override
                    public void failure(TwitterException error) {
                        refreshCompleted(error.getLocalizedMessage());
                    }
                }
        );
    }

    private boolean isNeedUpdateData(List<Tweet> data) {
        if (data != null && data.size() > 0) {
            long maxid = data.get(data.size() - 1).getId();
            TweetSelection selection = new TweetSelection().orderById(true).limit(1);
            TweetCursor cursor = selection.query(getContentResolver());

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                long id = cursor.getTweetId();
                cursor.close();
                return maxid != id;
            } else {
                return true;
            }

        }
        return false;
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

    private void refreshCompletedWithoutUpdate() {
        Intent intent = new Intent(ACTION_REFRESH_COMPLETED_WITOUT_UPDATE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyForceRefresh() {
        Intent intent = new Intent(ACTION_FORCE_REFRESH);
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
