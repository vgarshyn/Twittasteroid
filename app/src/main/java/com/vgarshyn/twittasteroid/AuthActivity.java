package com.vgarshyn.twittasteroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.vgarshyn.twittasteroid.core.Util;

public class AuthActivity extends Activity {
    private static final String TAG = AuthActivity.class.getSimpleName();

    private TwitterLoginButton mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchAuthWorkflow();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void dispatchAuthWorkflow() {
        if (isAuthSessionPresent()) {
            Toast.makeText(this, "Token present", Toast.LENGTH_LONG).show();
            startMainActivity();
            finish();
        } else {
            setContentView(R.layout.activity_auth);
            initUIelements();
        }
    }

    private void initUIelements() {
        mLoginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                startMainActivity();
                finish();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(getBaseContext(), "Auth failed :( " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean isAuthSessionPresent() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            TwitterAuthToken authToken = session.getAuthToken();
            String token = authToken.token;
            String secret = authToken.secret;
            return !Util.isEmpty(token, secret);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }

}
