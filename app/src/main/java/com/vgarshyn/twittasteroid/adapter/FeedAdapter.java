package com.vgarshyn.twittasteroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.R;
import com.vgarshyn.twittasteroid.core.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v.garshyn on 25.07.15.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private static final String TAG = FeedAdapter.class.getSimpleName();

    private List<Tweet> dataset = new ArrayList<Tweet>(0);

    public FeedAdapter() {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Tweet data = dataset.get(position);
        viewHolder.text.setText(data.text);
        Picasso.with(viewHolder.text.getContext())
                .load(Util.getTweetUserOriginalImageUrl(data.user.profileImageUrl))
                .resize(128, 128)
                .into(viewHolder.icon);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void updateDataSet(List<Tweet> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            text = (TextView) itemView.findViewById(R.id.text);
        }


    }
}
