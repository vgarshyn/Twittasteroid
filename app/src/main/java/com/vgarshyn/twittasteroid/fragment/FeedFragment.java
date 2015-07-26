package com.vgarshyn.twittasteroid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.vgarshyn.twittasteroid.R;
import com.vgarshyn.twittasteroid.adapter.FeedAdapter;
import com.vgarshyn.twittasteroid.core.ui.EndlessScrollListener;

import java.util.List;

/**
 * Created by v.garshyn on 25.07.15.
 */
public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = FeedFragment.class.getSimpleName();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FeedAdapter mFeedAdapter;
    private RecyclerView mRecyclerView;
    private long mLastId;
    private LinearLayoutManager layoutManager;

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
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadTweets(mLastId);
            }
        });
//        loadTweets(0);
    }

    public void loadTweets(long lastid) {
        final StatusesService service = Twitter.getInstance().getApiClient().getStatusesService();
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
        );
    }

    private void cancelRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        loadTweets(0);
    }
}
