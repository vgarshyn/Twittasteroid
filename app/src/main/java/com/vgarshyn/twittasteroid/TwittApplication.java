package com.vgarshyn.twittasteroid;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by v.garshyn on 23.07.15.
 */
public class TwittApplication extends Application {
    private static final String TAG = TwittApplication.class.getSimpleName();
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "RwpDXcYjyV065Et3gxchK7g4S";
    private static final String TWITTER_SECRET = "3cBp8LlOOd72kNUltqtfOYAUuVgJXlbsqnyGrZ05SYWGdyjoAp ";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }
}
