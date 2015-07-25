package com.vgarshyn.twittasteroid.fragment;

import android.app.Fragment;
import android.os.Bundle;
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

import java.util.List;

/**
 * Created by v.garshyn on 25.07.15.
 */
public class FeedFragment extends Fragment {
    private static final String TAG = FeedFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private FeedAdapter mFeedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.feed_list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFeedAdapter = new FeedAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFeedAdapter);
        loadTweets();
    }

    public void loadTweets() {
        final StatusesService service = Twitter.getInstance().getApiClient().getStatusesService();

        service.homeTimeline(null, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        mFeedAdapter.updateDataSet(result.data);
                    }

                    @Override
                    public void failure(TwitterException error) {
                        Toast.makeText(getActivity(), "Failed to retrieve timeline",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
