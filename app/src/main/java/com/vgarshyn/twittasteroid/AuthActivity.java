package com.vgarshyn.twittasteroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.vgarshyn.twittasteroid.core.Util;
import com.vgarshyn.twittasteroid.core.ui.LogoAnimation;

public class AuthActivity extends Activity {
    private static final String TAG = AuthActivity.class.getSimpleName();

    private TwitterLoginButton mLoginButton;
    private ImageView mImageLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchAuthWorkflow();
    }

    private void dispatchAuthWorkflow() {
        if (isAuthSessionPresent()) {
            startMainActivity();
            finish();
        } else {
            setContentView(R.layout.activity_auth);
            initUIelements();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initUIelements() {
        mLoginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mLoginButton.setVisibility(View.INVISIBLE);
                mLoginButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startMainActivityWithAnimation();
                    }
                }, 1000);
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(getBaseContext(), "Auth failed :( " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mImageLogo = (ImageView) findViewById(R.id.icon_logo);
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

    private void startMainActivityWithAnimation() {
        Animation animation = new LogoAnimation(getBaseContext());
        animation.setAnimationListener(new LogoAnimation.SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                startMainActivity();
            }
        });
        mImageLogo.startAnimation(animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }

}
