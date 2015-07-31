package com.vgarshyn.twittasteroid.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.UrlEntity;
import com.vgarshyn.twittasteroid.core.ui.ClickableUrlSpan;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by v.garshyn on 23.07.15.
 */
public final class Util {
    public static final String EMPTY_STRING = "";
    private static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;
    private static final String PHOTO_TYPE = "photo";
    private static final String END_TRUNCATED_LINK = "…";
    private static final String HTTP_TEXT = "http";

    private static final SimpleDateFormat TWITTER_TIMESTAMP = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

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

    /**
     * Calculate ratio for best image scaling
     *
     * @param photoEntity
     * @return
     */
    public static double getAspectRatio(MediaEntity photoEntity) {
        if (photoEntity == null || photoEntity.sizes == null || photoEntity.sizes.medium == null ||
                photoEntity.sizes.medium.w == 0 || photoEntity.sizes.medium.h == 0) {
            return DEFAULT_ASPECT_RATIO;
        }

        return (double) photoEntity.sizes.medium.w / photoEntity.sizes.medium.h;
    }

    /**
     * Check is tweet contains video link.
     * NOTICE: Now twitter API not return valid entities with video...
     *
     * @param tweet
     * @return
     */
    public static boolean isContainsTweetVideo(Tweet tweet) {
        if (tweet.entities != null) {
            List<UrlEntity> urlEntities = tweet.entities.urls;
            if (urlEntities != null && !urlEntities.isEmpty()) {
                UrlEntity entity;
                for (int i = urlEntities.size() - 1; i >= 0; i--) {
                    entity = urlEntities.get(i);
                    return !TextUtils.isEmpty(entity.expandedUrl) && entity.expandedUrl.startsWith("https://amp.twimg.com");
                }
            }
        }
        return false;
    }

    /**
     * Detect is tweet contains youtube link
     *
     * @param tweet
     * @return
     */
    public static boolean isContainsYoutubeVideo(Tweet tweet) {
        if (tweet.entities != null) {
            List<UrlEntity> urlEntities = tweet.entities.urls;
            if (urlEntities != null && !urlEntities.isEmpty()) {
                UrlEntity entity;
                for (int i = urlEntities.size() - 1; i >= 0; i--) {
                    entity = urlEntities.get(i);
                    return !TextUtils.isEmpty(entity.expandedUrl) && entity.expandedUrl.contains("youtube.com");
                }
            }
        }
        return false;
    }

    /**
     * Return youtube first url
     *
     * @param tweet
     * @return
     */
    public static String getYoutubeVideoUrl(Tweet tweet) {
        if (tweet.entities != null) {
            List<UrlEntity> urlEntities = tweet.entities.urls;
            if (urlEntities != null && !urlEntities.isEmpty()) {
                UrlEntity entity;
                for (int i = urlEntities.size() - 1; i >= 0; i--) {
                    entity = urlEntities.get(i);
                    if (!TextUtils.isEmpty(entity.expandedUrl) && entity.expandedUrl.contains("youtube.com")) {
                        return entity.expandedUrl;
                    }
                }
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Extract youtube id from url with regexp
     *
     * @param url
     * @return
     */
    public static String extractYoutubeVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return EMPTY_STRING;
    }


    /**
     * Very simple generate youtube preview link base on youtube id
     * <p/>
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/0.jpg
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/1.jpg
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/2.jpg
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/3.jpg
     * The first one in the list is a full size image and others are thumbnail images. The default thumbnail image (ie. one of 1.jpg, 2.jpg, 3.jpg) is:
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/default.jpg
     * For the high quality version of the thumbnail use a url similar to this:
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/hqdefault.jpg
     * There is also a medium quality version of the thumbnail, using a url similar to the HQ:
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg
     * For the standard definition version of the thumbnail, use a url similar to this:
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/sddefault.jpg
     * For the maximum resolution version of the thumbnail use a url similar to this:
     * http://img.youtube.com/vi/<insert-youtube-video-id-here>/maxresdefault.jpg
     *
     * @param youtubeId
     * @return
     */
    public static String getYoutubePreviewUrl(String youtubeId) {
        return "http://img.youtube.com/vi/" + youtubeId + "/hqdefault.jpg";
    }

    /**
     * Format twitter timestamp into user readable date
     *
     * @param apiTime
     * @return
     */
    public static String formatDate(String apiTime) {
        if (apiTime == null) {
            return EMPTY_STRING;
        }
        try {
            long datetime = TWITTER_TIMESTAMP.parse(apiTime).getTime();
            return DATE_FORMAT.format(datetime);
        } catch (ParseException e) {
            return EMPTY_STRING;
        }
    }

    /**
     * Render tweet text into spannable string with links, marked hashtags and user mentions.
     *
     * @param tweet
     * @param colorLink
     * @param colorHashTag
     * @param colorMention
     * @return
     */
    public static CharSequence getLinkifiedText(Tweet tweet, int colorLink, int colorHashTag, int colorMention) {
        String text = tweet.text;
        if (tweet.entities != null) {

            List<MediaEntity> mediaEntityList = tweet.entities.media;
            if (mediaEntityList != null && !mediaEntityList.isEmpty()) {
                MediaEntity entity;
                for (int i = mediaEntityList.size() - 1; i >= 0; i--) {
                    entity = mediaEntityList.get(i);
                    if (entity.type != null && entity.type.equals(PHOTO_TYPE)) {
                        text = text.replace(entity.url, "");
                    }
                }
            }

            if (text.trim().endsWith(END_TRUNCATED_LINK)) {
                int lastindex = text.lastIndexOf(HTTP_TEXT);
                if (lastindex > 0) {
                    text = text.substring(0, lastindex);
                }
            }

            SpannableString spannableString = new SpannableString(text);

            List<UrlEntity> urlEntities = tweet.entities.urls;

            if (urlEntities != null && !urlEntities.isEmpty()) {
                for (UrlEntity entity : urlEntities) {
                    String url = entity.url;
                    if (text.contains(url)) {
                        int start = text.indexOf(url);
                        int end = start + url.length();
                        spannableString.setSpan(new ForegroundColorSpan(colorLink), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new ClickableUrlSpan(entity.expandedUrl), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            List<HashtagEntity> hashEntities = tweet.entities.hashtags;

            if (hashEntities != null && !hashEntities.isEmpty()) {
                for (HashtagEntity entity : hashEntities) {
                    String tag = "#" + entity.text;
                    if (text.contains(tag)) {
                        int start = text.indexOf(tag);
                        int end = start + tag.length();
                        spannableString.setSpan(new ForegroundColorSpan(colorHashTag), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }


            List<MentionEntity> mentionEntities = tweet.entities.userMentions;

            if (mentionEntities != null && !mentionEntities.isEmpty()) {
                for (MentionEntity entity : mentionEntities) {
                    String screenName = "@" + entity.screenName;
                    if (text.contains(screenName)) {
                        int start = text.indexOf(screenName);
                        int end = start + screenName.length();
                        spannableString.setSpan(new ForegroundColorSpan(colorMention), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            return spannableString;
        }
        return text;
    }

    /**
     * Check is network available
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * There is a possibility device connected to network but have not internet connection.
     * This method check it.
     *
     * @return
     */
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("twitter.com");
            return !ipAddr.equals(EMPTY_STRING);
        } catch (Exception e) {
            return false;
        }
    }
}