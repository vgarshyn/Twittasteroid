package com.vgarshyn.twittasteroid.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.models.User;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Helper class to store data about loged user into shared preferences
 *
 * Created by v.garshyn on 30.07.15.
 */
public class UserPreferences {
    private static final String TAG = UserPreferences.class.getSimpleName();

    private static final String AVATAR_FILENAME = "avatar.pic";

    private static final String PREF_NAME = "user";
    private static final String KEY_USERNAME = "key.username";
    private static final String KEY_SCREENNAME = "key.screenname";
    private static final String KEY_AVATAR_URL = "key.avataturl";


    private SharedPreferences preferences;
    private File cacheDirectory;
    private File avatarFile;

    public UserPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        cacheDirectory = context.getCacheDir();
        avatarFile = new File(cacheDirectory, AVATAR_FILENAME);
    }

    public void subscribe(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unSubscribe(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void storeUser(Context context, User user) {
        if (user != null) {
            preferences.edit()
                    .putString(KEY_USERNAME, user.name)
                    .putString(KEY_SCREENNAME, user.screenName)
                    .putString(KEY_AVATAR_URL, user.profileImageUrl)
                    .apply();
            if (!avatarFile.exists()) {
                String url = Util.getTweetUserReasonableImageUrl(user.profileImageUrl);
                downloadImage(context, url);
            }
        }
    }

    public boolean isStored() {
        return preferences.contains(KEY_USERNAME);
    }

    public String getUserName() {
        return preferences.getString(KEY_USERNAME, Util.EMPTY_STRING);
    }

    public String getScreenName() {
        return preferences.getString(KEY_SCREENNAME, Util.EMPTY_STRING);
    }

    public String getAvatarUrl() {
        return preferences.getString(KEY_AVATAR_URL, Util.EMPTY_STRING);
    }

    public boolean isCachedAvatarExists() {
        return avatarFile.exists();
    }

    public File getAvatarFile() {
        return avatarFile;
    }

    private void downloadImage(final Context context, final String imageUrl) {
        Target target = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
                if (avatarFile.exists()) {
                    avatarFile.delete();
                }
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                try {
                    FileOutputStream ostream = new FileOutputStream(avatarFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable arg0) {
                return;
            }
        };

        Picasso.with(context)
                .load(imageUrl)
                .into(target);
    }

}
