package com.vgarshyn.twittasteroid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;
import com.vgarshyn.twittasteroid.R;
import com.vgarshyn.twittasteroid.core.UserPreferences;
import com.vgarshyn.twittasteroid.core.Util;

/**
 * Created by v.garshyn on 30.07.15.
 */
public class MenuFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MenuFragment.class.getSimpleName();

    private ImageView mUserAvatar;
    private TextView mUserName;
    private TextView mScreenName;

    private UserPreferences preferences;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        preferences = new UserPreferences(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserAvatar = (ImageView) view.findViewById(R.id.user_avatar);
        mUserName = (TextView) view.findViewById(R.id.text_user_name);
        mScreenName = (TextView) view.findViewById(R.id.text_screen_name);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (preferences.isStored()) {
            updateInfo();
        }
        if (Util.isNetworkAvailable(getActivity())) {
            getUserInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        preferences.subscribe(this);
    }

    @Override
    public void onPause() {
        preferences.unSubscribe(this);
        super.onPause();
    }

    private void getUserInfo() {
        Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                if (getActivity() != null && userResult.data != null) {
                    preferences.storeUser(getActivity(), userResult.data);
                }
            }

            @Override
            public void failure(TwitterException e) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateInfo();
    }


    public void updateInfo() {
        if (preferences.isCachedAvatarExists()) {
            Picasso.with(getActivity())
                    .load(preferences.getAvatarUrl())
                    .into(mUserAvatar);
        } else {
            String url = Util.getTweetUserReasonableImageUrl(preferences.getAvatarUrl());
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(getActivity())
                        .load(url)
                        .into(mUserAvatar);
            }
        }
        mUserName.setText("@" + preferences.getUserName());
        mScreenName.setText(preferences.getScreenName());

    }
}
