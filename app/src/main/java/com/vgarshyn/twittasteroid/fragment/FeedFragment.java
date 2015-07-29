package com.vgarshyn.twittasteroid.fragment;

import android.app.Activity;
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
import com.vgarshyn.twittasteroid.core.ui.DividerItemDecoration;
import com.vgarshyn.twittasteroid.core.ui.EndlessScrollListener;

import java.util.List;

/**
 * Created by v.garshyn on 25.07.15.
 */
public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Tweet>> {
    private static final String TAG = FeedFragment.class.getSimpleName();

    private static final String ARG_SINCE_ID = "arg.since_id";
    private static final int LOADER_ID_REFRESH = 23;
    private static final int LOADER_ID_LOAD_MORE = 24;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FeedAdapter mFeedAdapter;
    private RecyclerView mRecyclerView;
    private long mLastId;
    private LinearLayoutManager layoutManager;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TweetIntentService.ACTION_REFRESH_COMPLETED.equals(action)) {
                if (intent.hasExtra(TweetIntentService.EXTRA_ERROR)) {
                    Toast.makeText(getActivity(), intent.getStringExtra(TweetIntentService.EXTRA_ERROR), Toast.LENGTH_LONG).show();
                } else {
//                    getLoaderManager().restartLoader(LOADER_ID_REFRESH, null, FeedFragment.this);
                    getLoaderManager().initLoader(LOADER_ID_REFRESH, null, FeedFragment.this);
                }
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
            }

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LocalBroadcastManager.getInstance(activity).registerReceiver(
                mMessageReceiver, new IntentFilter(TweetIntentService.ACTION_REFRESH_COMPLETED));
        LocalBroadcastManager.getInstance(activity).registerReceiver(
                mMessageReceiver, new IntentFilter(TweetIntentService.ACTION_LOAD_MORE_COMPLETED));
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFeedAdapter = new FeedAdapter();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.list_divider));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadTweets(mFeedAdapter.getLastTweetId());
            }
        });
//        getLoaderManager().initLoader(LOADER_ID_REFRESH, null, this);
        getLoaderManager().initLoader(LOADER_ID_LOAD_MORE, null, this);
    }

    public void loadTweets(Long lastid) {
        TweetIntentService.startActionLoadMore(getActivity(), lastid);
  /*      final StatusesService service = Twitter.getInstance().getApiClient().getStatusesService();
        Long lastId = lastid == 0 ? null : lastid;
        service.homeTimeline(50, null, lastId, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {

                        List<Tweet> data = result.data;
                        if (data != null && data.size() > 0) {
                            mFeedAdapter.updateDataSet(data);
                            mLastId = data.get(data.size() - 1).getId();
                        }
                        cancelRefresh();
                    }

                    @Override
                    public void failure(TwitterException error) {
                        Toast.makeText(getActivity(), "Failed to retrieve timeline",
                                Toast.LENGTH_SHORT).show();
                        cancelRefresh();
                    }
                }
        );*/
    }

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
                mFeedAdapter.updateDataSet(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Tweet>> loader) {

    }


}
