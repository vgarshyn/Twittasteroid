package com.vgarshyn.twittasteroid.contentprovider.tweet;

import android.net.Uri;
import android.provider.BaseColumns;

import com.vgarshyn.twittasteroid.contentprovider.TweetContentProvider;

/**
 * Columns for the {@code tweet} table.
 *
 * Created by v.garshyn on 26.07.15.
 */
public class TweetColumns implements BaseColumns {
    public static final String TABLE_NAME = "tweet";
    public static final Uri CONTENT_URI = Uri.parse(TweetContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String TWEET_ID = "tweet_id";

    public static final String CREATED_AT = "created_at";

    public static final String ORIGINAL_JSON = "original_json";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[]{
            _ID,
            TWEET_ID,
            CREATED_AT,
            ORIGINAL_JSON
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(TWEET_ID) || c.contains("." + TWEET_ID)) return true;
            if (c.equals(CREATED_AT) || c.contains("." + CREATED_AT)) return true;
            if (c.equals(ORIGINAL_JSON) || c.contains("." + ORIGINAL_JSON)) return true;
        }
        return false;
    }

}
