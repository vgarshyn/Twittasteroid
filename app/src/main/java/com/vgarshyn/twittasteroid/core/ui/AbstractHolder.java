package com.vgarshyn.twittasteroid.core.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twitter.sdk.android.core.models.Tweet;

/**
 * Created by v.garshyn on 29.07.15.
 */
public abstract class AbstractHolder extends RecyclerView.ViewHolder {
    
    public AbstractHolder(View itemView) {
        super(itemView);
    }

    /**
     * Display data on layout item
     *
     * @param data
     */
    public abstract void render(Tweet data);
}
