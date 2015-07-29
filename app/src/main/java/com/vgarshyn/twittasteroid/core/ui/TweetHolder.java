package com.vgarshyn.twittasteroid.core.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.internal.util.AspectRatioImageView;
import com.vgarshyn.twittasteroid.PhotoViewActivity;
import com.vgarshyn.twittasteroid.R;
import com.vgarshyn.twittasteroid.core.Util;

/**
 * Created by v.garshyn on 26.07.15.
 */
public class TweetHolder extends RecyclerView.ViewHolder {
    private static final String TAG = TweetHolder.class.getSimpleName();
    final int colorFullname;
    final int colorUsername;
    TextView textAuthor;
    TextView textContent;
    TextView textTime;
    ImageView imageUser;
    AspectRatioImageView imageTweetPhoto;
    Context context;

    TweetHolder(View itemView) {
        super(itemView);
        context = itemView.getContext().getApplicationContext();
        textContent = (TextView) itemView.findViewById(R.id.text_tweet_content);
        textAuthor = (TextView) itemView.findViewById(R.id.text_author);
        textTime = (TextView) itemView.findViewById(R.id.text_time);
        imageUser = (ImageView) itemView.findViewById(R.id.image_user_profile);
        imageTweetPhoto = (AspectRatioImageView) itemView.findViewById(R.id.image_tweet_photo);
        colorFullname = context.getResources().getColor(R.color.tweet_fullname);
        colorUsername = context.getResources().getColor(R.color.tweet_username);
    }

    /**
     * Factory method to instantiate ViewHolder
     *
     * @param parent
     * @return
     */
    public static TweetHolder instantiate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new TweetHolder(view);
    }

    /**
     * Display tweet data on item layout
     *
     * @param tweet
     */
    public void render(Tweet tweet) {
        textContent.setText(tweet.text);
        textAuthor.setText(tweet.user.name);
        showUserName(tweet);
        showTimestamp(tweet.createdAt);
        showUserPhoto(tweet);
        showTweetPhoto(tweet);
        if (Util.isContainsVideo(tweet)) {
            Log.e(TAG, "Video contains: true");
        }
    }

    /**
     * Download and display in ImageView user photo with Picasso lib
     *
     * @param tweet
     */
    private void showUserPhoto(Tweet tweet) {
        Picasso.with(context)
                .load(Util.getTweetUserReasonableImageUrl(tweet.user.profileImageUrl))
                .placeholder(R.mipmap.ic_placeholder_twitter)
                .into(imageUser);

    }

    /**
     * Format username and display name in one string with richtext features.
     * Display obtained richtext into appropriate TextView
     *
     * @param tweet
     */
    private void showUserName(Tweet tweet) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        SpannableString fullname = new SpannableString(tweet.user.name);
        fullname.setSpan(new StyleSpan(Typeface.BOLD), 0, fullname.length(), 0);
        fullname.setSpan(new ForegroundColorSpan(colorFullname), 0, fullname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fullname.setSpan(new StyleSpan(Typeface.BOLD), 0, fullname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString nickname = new SpannableString("@" + tweet.user.screenName);
        nickname.setSpan(new ForegroundColorSpan(colorUsername), 0, nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.append(fullname);
        ssb.append("  ");
        ssb.append(nickname);

        textAuthor.setText(ssb);
    }

    /**
     * Check availability of media entity with photo in twitter message.
     * If available then show it proportionally scaled.
     *
     * @param displayTweet
     */
    private void showTweetPhoto(Tweet displayTweet) {
        final MediaEntity entity = Util.getLastPhotoEntity(displayTweet);
        clearMediaBackground();
        if (entity != null) {
            imageTweetPhoto.resetSize();
            imageTweetPhoto.setAspectRatio(Util.getAspectRatio(entity));
            imageTweetPhoto.setVisibility(ImageView.VISIBLE);
            Picasso.with(context)
                    .load(entity.mediaUrl)
                    .into(imageTweetPhoto);
            imageTweetPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(PhotoViewActivity.EXTRA_PHOTO_URL, entity.mediaUrl);
                    context.startActivity(intent);
                }
            });
        } else {
            imageTweetPhoto.setVisibility(ImageView.GONE);
        }
    }

    /**
     * Format twitter time stamp into readable data.
     * <p/>
     * current format: dd/MM/yyyy HH:mm
     *
     * @param createdAt
     */
    private void showTimestamp(String createdAt) {
        String formattedTimestamp = Util.formatDate(createdAt);
        if (!TextUtils.isEmpty(formattedTimestamp)) {
            textTime.setText(formattedTimestamp);
            textTime.setVisibility(View.VISIBLE);
        } else {
            textTime.setVisibility(View.GONE);
        }
    }

    /**
     * Clear background data for AspectRatioImageView
     * because twitter photo display as background on this kind of View
     */
    protected void clearMediaBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageTweetPhoto.setBackground(null);
        } else {
            imageTweetPhoto.setBackgroundDrawable(null);
        }
    }

}
