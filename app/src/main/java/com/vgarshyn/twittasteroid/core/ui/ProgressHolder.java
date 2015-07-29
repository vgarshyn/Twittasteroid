package com.vgarshyn.twittasteroid.core.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.twitter.sdk.android.core.models.Tweet;
import com.vgarshyn.twittasteroid.R;

/**
 * Created by v.garshyn on 29.07.15.
 */
public class ProgressHolder extends AbstractHolder {
    public ProgressBar progressBar;

    ProgressHolder(View view) {
        super(view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    public static ProgressHolder instantiate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
        return new ProgressHolder(view);
    }

    @Override
    public void render(Tweet data) {
        progressBar.setIndeterminate(true);
    }
}
