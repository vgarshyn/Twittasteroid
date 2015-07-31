package com.vgarshyn.twittasteroid.contentprovider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.vgarshyn.twittasteroid.BuildConfig;
import com.vgarshyn.twittasteroid.contentprovider.base.BaseContentProvider;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetColumns;

import java.util.Arrays;

/**
 * Content provider with main CRUD operations
 * <p/>
 * Created by v.garshyn on 26.07.15.
 */
public class TweetContentProvider extends BaseContentProvider {
    public static final String TRUNCATE_COMMAND = "truncate";
    public static final String AUTHORITY = "com.twittasteroid";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;
    private static final String TAG = TweetContentProvider.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";
    private static final int URI_TYPE_TWEET = 0;
    private static final int URI_TYPE_TWEET_ID = 1;


    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, TweetColumns.TABLE_NAME, URI_TYPE_TWEET);
        URI_MATCHER.addURI(AUTHORITY, TweetColumns.TABLE_NAME + "/#", URI_TYPE_TWEET_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return TweetSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_TWEET:
                return TYPE_CURSOR_DIR + TweetColumns.TABLE_NAME;
            case URI_TYPE_TWEET_ID:
                return TYPE_CURSOR_ITEM + TweetColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG)
            Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG)
            Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        if (TRUNCATE_COMMAND.equals(selection)) {
            try {
                mSqLiteOpenHelper.getWritableDatabase().delete(TweetColumns.TABLE_NAME, null, null);
                getContext().getContentResolver().notifyChange(uri, null);
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_TWEET:
            case URI_TYPE_TWEET_ID:
                res.table = TweetColumns.TABLE_NAME;
                res.idColumn = TweetColumns._ID;
                res.tablesWithJoins = TweetColumns.TABLE_NAME;
                res.orderBy = TweetColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_TWEET_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
