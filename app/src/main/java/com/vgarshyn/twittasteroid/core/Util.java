package com.vgarshyn.twittasteroid.core;

import android.text.TextUtils;

/**
 * Created by v.garshyn on 23.07.15.
 */
public final class Util {

    /**
     * Check is empty for multiple strings, sometimes it very useful
     *
     * @param strings
     * @return
     */
    public static boolean isEmpty(String... strings) {
        if (strings != null && strings.length > 0) {
            for (String string : strings) {
                if (TextUtils.isEmpty(string)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Modify url to retrieve big user image
     * details: https://dev.twitter.com/overview/general/user-profile-images-and-banners
     *
     * @param url
     * @return
     */
    public static String getTweetUserOriginalImageUrl(String url) {
        if (!TextUtils.isEmpty(url) && url.length() > 10) {
            return url.replace("_normal", "");
        }
        return url;
    }

}
