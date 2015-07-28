package com.vgarshyn.twittasteroid.contentprovider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.vgarshyn.twittasteroid.BuildConfig;
import com.vgarshyn.twittasteroid.contentprovider.tweet.TweetColumns;

public class TweetSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_FILE_NAME = "tweets.db";
    // @formatter:off
    public static final String SQL_CREATE_TABLE_TWEET = "CREATE TABLE IF NOT EXISTS "
            + TweetColumns.TABLE_NAME + " ( "
            + TweetColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TweetColumns.TWEET_ID + " INTEGER NOT NULL, "
            + TweetColumns.CREATED_AT + " TEXT, "
            + TweetColumns.ORIGINAL_JSON + " TEXT "
            + ", CONSTRAINT unique_name UNIQUE (tweet_id) ON CONFLICT REPLACE"
            + " );";
    public static final String SQL_CREATE_INDEX_TWEET_TWEET_ID = "CREATE INDEX IDX_TWEET_TWEET_ID "
            + " ON " + TweetColumns.TABLE_NAME + " ( " + TweetColumns.TWEET_ID + " );";
    private static final String TAG = TweetSQLiteOpenHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static TweetSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final TweetSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:on

    private TweetSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new TweetSQLiteOpenHelperCallbacks();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private TweetSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new TweetSQLiteOpenHelperCallbacks();
    }

    public static TweetSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static TweetSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }

    /*
     * Pre Honeycomb.
     */
    private static TweetSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new TweetSQLiteOpenHelper(context);
    }

    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static TweetSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new TweetSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_TWEET);
        db.execSQL(SQL_CREATE_INDEX_TWEET_TWEET_ID);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
