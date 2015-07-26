package com.vgarshyn.twittasteroid.core;

import android.text.TextUtils;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Created by v.garshyn on 23.07.15.
 */
public final class Util {
    private static final String PHOTO_TYPE = "photo";

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
    public static String getTweetUserReasonableImageUrl(String url) {
        if (!TextUtils.isEmpty(url) && url.length() > 10) {
            return url.replace("_normal", "_reasonably_small");
        }
        return url;
    }

    /**
     * Extract last photo from tweet
     *
     * @param tweet
     * @return
     */
    public static MediaEntity getLastPhotoEntity(Tweet tweet) {
        if (tweet.entities != null) {
            List<MediaEntity> mediaEntityList = tweet.entities.media;
            if (mediaEntityList != null && !mediaEntityList.isEmpty()) {
                MediaEntity entity;
                for (int i = mediaEntityList.size() - 1; i >= 0; i--) {
                    entity = mediaEntityList.get(i);
                    if (entity.type != null && entity.type.equals(PHOTO_TYPE)) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }

}