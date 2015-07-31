package com.vgarshyn.twittasteroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.core.ui.AbstractHolder;
import com.vgarshyn.twittasteroid.core.ui.ProgressHolder;
import com.vgarshyn.twittasteroid.core.ui.TweetHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for represent feed in RecylerView
 *
 * Created by v.garshyn on 25.07.15.
 */
public class FeedAdapter extends RecyclerView.Adapter<AbstractHolder> {
    private static final String TAG = FeedAdapter.class.getSimpleName();

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROGRESS = 0;

    private List<Tweet> dataset = new ArrayList<Tweet>(0);

    public FeedAdapter() {
        super();
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == VIEW_ITEM ? TweetHolder.instantiate(parent) : ProgressHolder.instantiate(parent);
    }

    @Override
    public void onBindViewHolder(AbstractHolder viewHolder, int position) {
        Tweet tweet = dataset.get(position);
        viewHolder.render(tweet);
    }

    @Override
    public int getItemViewType(int position) {
        return dataset.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void updateDataSet(List<Tweet> dataset) {
        this.dataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void refreshDataSet(List<Tweet> dataset) {
        this.dataset.clear();
        notifyDataSetChanged();
        this.dataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public Long getLastTweetId() {
        int size = dataset.size();
        if (size > 0 && dataset.get(size - 1) != null) {
            return dataset.get(size - 1).getId();
        }
        return null;
    }

    public void showProgressBarFooter() {
        dataset.add(null);
        notifyItemInserted(dataset.size() - 1);
    }

    public void hideProgressBarFooter() {
        if (dataset.size() > 0) {
            dataset.remove(dataset.size() - 1);
            notifyItemRemoved(dataset.size());
        }
    }
}
