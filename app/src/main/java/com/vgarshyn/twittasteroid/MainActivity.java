package com.vgarshyn.twittasteroid;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.vgarshyn.twittasteroid.core.Util;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "RwpDXcYjyV065Et3gxchK7g4S";
    private static final String TWITTER_SECRET = "3cBp8LlOOd72kNUltqtfOYAUuVgJXlbsqnyGrZ05SYWGdyjoAp ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        if (initializer()) {
            Toast.makeText(this, "Token present", Toast.LENGTH_LONG).show();
        }
    }

    private boolean initializer() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;
        return !Util.isEmpty(token, secret);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.login_fragment);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
