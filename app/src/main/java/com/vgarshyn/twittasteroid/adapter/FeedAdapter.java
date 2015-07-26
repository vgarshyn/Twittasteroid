package com.vgarshyn.twittasteroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.core.ui.TweetHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v.garshyn on 25.07.15.
 */
public class FeedAdapter extends RecyclerView.Adapter<TweetHolder> {
    private static final String TAG = FeedAdapter.class.getSimpleName();

    private List<Tweet> dataset = new ArrayList<Tweet>(0);

    public FeedAdapter() {
        super();
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TweetHolder.instantiate(parent);
    }

    @Override
    public void onBindViewHolder(TweetHolder viewHolder, int position) {
        Tweet tweet = dataset.get(position);
        viewHolder.render(tweet);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void updateDataSet(List<Tweet> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }

}
