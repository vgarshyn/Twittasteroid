package com.vgarshyn.twittasteroid.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.R;
import com.vgarshyn.twittasteroid.adapter.FeedAdapter;
import com.vgarshyn.twittasteroid.core.TweetDataLoader;
import com.vgarshyn.twittasteroid.core.TweetIntentService;
import com.vgarshyn.twittasteroid.core.ui.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to represent twitter feed
 *
 * Created by v.garshyn on 25.07.15.
 */
public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Tweet>> {
    private static final String TAG = FeedFragment.class.getSimpleName();

    private static final String ARG_SINCE_ID = "arg.since_id";
    private static final int LOADER_ID_REFRESH = 2023;
    private static final int LOADER_ID_LOAD_MORE = 2024;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FeedAdapter mFeedAdapter;
    private RecyclerView mRecyclerView;

    private LinearLayoutManager layoutManager;
    private EndlessScrollListener mEndlessScrollListener;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TweetIntentService.ACTION_REFRESH_COMPLETED.equals(action)) {
                if (intent.hasExtra(TweetIntentService.EXTRA_ERROR)) {
                    cancelRefresh();
                    Toast.makeText(getActivity(), intent.getStringExtra(TweetIntentService.EXTRA_ERROR), Toast.LENGTH_LONG).show();
                } else {
                    getLoaderManager().restartLoader(LOADER_ID_REFRESH, null, FeedFragment.this);
                }
            }
            if (TweetIntentService.ACTION_REFRESH_COMPLETED_WITOUT_UPDATE.equals(action)) {
                cancelRefresh();
            } else if (TweetIntentService.ACTION_LOAD_MORE_COMPLETED.equals(action)) {
                if (intent.hasExtra(TweetIntentService.EXTRA_ERROR)) {
                    Toast.makeText(getActivity(), intent.getStringExtra(TweetIntentService.EXTRA_ERROR), Toast.LENGTH_LONG).show();
                } else {
                    Bundle bundle = new Bundle();
                    if (intent.hasExtra(TweetIntentService.EXTRA_MAX_ID)) {
                        bundle.putLong(ARG_SINCE_ID, intent.getLongExtra(TweetIntentService.EXTRA_MAX_ID, 0));
                    }
                    getLoaderManager().restartLoader(LOADER_ID_LOAD_MORE, bundle, FeedFragment.this);
                }
            } else if (TweetIntentService.ACTION_FORCE_REFRESH.equals(action)) {
                Toast.makeText(getActivity(), R.string.message_data_invalidated, Toast.LENGTH_LONG).show();
                mFeedAdapter.refreshDataSet(new ArrayList<Tweet>(0));
            }

        }
    };

    private void registerReceivers(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TweetIntentService.ACTION_REFRESH_COMPLETED);
        filter.addAction(TweetIntentService.ACTION_REFRESH_COMPLETED_WITOUT_UPDATE);
        filter.addAction(TweetIntentService.ACTION_LOAD_MORE_COMPLETED);
        filter.addAction(TweetIntentService.ACTION_FORCE_REFRESH);
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, filter);
    }

    private void unregisterReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.feed_list);
        layoutManager = new LinearLayoutManager(getActivity());
        mEndlessScrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                setExternalLoadingFlag(true);
                loadMoreTweets(mFeedAdapter.getLastTweetId());
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFeedAdapter = new FeedAdapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(mEndlessScrollListener);

        initFeedAndLoadFresh(savedInstanceState == null);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers(getActivity());
    }

    @Override
    public void onPause() {
        unregisterReceivers(getActivity());
        super.onPause();
    }

    /**
     * Call on app start. Show old tweets from DB and simultaneously load new data and refresh all when complete.
     */
    private void initFeedAndLoadFresh(boolean updateFreshFeed) {
        if (updateFreshFeed) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    TweetIntentService.startActionRefresh(getActivity());
                }
            });
        }
        getLoaderManager().initLoader(LOADER_ID_LOAD_MORE, null, this);
    }

    /**
     * Infinite tweets pagination
     *
     * @param lastid
     */
    private void loadMoreTweets(Long lastid) {
        mFeedAdapter.showProgressBarFooter();
        TweetIntentService.startActionLoadMore(getActivity(), lastid);
    }

    /**
     * Hide pull to refresh progress bar
     */
    private void cancelRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        TweetIntentService.startActionRefresh(getActivity());
    }

    @Override
    public Loader<List<Tweet>> onCreateLoader(int id, Bundle args) {
        TweetDataLoader loader;
        if (args != null && args.containsKey(ARG_SINCE_ID)) {
            loader = new TweetDataLoader(getActivity(), args.getLong(ARG_SINCE_ID, 0));
        } else {
            loader = new TweetDataLoader(getActivity());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Tweet>> loader, List<Tweet> data) {
        switch (loader.getId()) {
            case LOADER_ID_REFRESH:
                cancelRefresh();
                mFeedAdapter.refreshDataSet(data);
                break;
            case LOADER_ID_LOAD_MORE:
                mFeedAdapter.hideProgressBarFooter();
                mEndlessScrollListener.setExternalLoadingFlag(false);
                mFeedAdapter.updateDataSet(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Tweet>> loader) {

    }


}
