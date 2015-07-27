package com.vgarshyn.twittasteroid.core.ui;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.internal.util.AspectRatioImageView;
import com.vgarshyn.twittasteroid.R;
import com.vgarshyn.twittasteroid.core.Util;

/**
 * Created by v.garshyn on 26.07.15.
 */
public class TweetHolder extends RecyclerView.ViewHolder {
    private static final String TAG = TweetHolder.class.getSimpleName();
    TextView textAuthor;
    TextView textContent;
    ImageView imageUser;
    AspectRatioImageView imageTweetPhoto;
    Context context;

    TweetHolder(View itemView) {
        super(itemView);
        context = itemView.getContext().getApplicationContext();
        textContent = (TextView) itemView.findViewById(R.id.text_tweet_content);
        textAuthor = (TextView) itemView.findViewById(R.id.text_author);
        imageUser = (ImageView) itemView.findViewById(R.id.image_user_profile);
        imageTweetPhoto = (AspectRatioImageView) itemView.findViewById(R.id.image_tweet_photo);
    }

    public static TweetHolder instantiate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new TweetHolder(view);
    }

    public void render(Tweet tweet) {
        textContent.setText(tweet.text);
        textAuthor.setText(tweet.user.name);
        showUserPhoto(tweet);
        showTweetPhoto(tweet);
    }

    private void showUserPhoto(Tweet tweet) {
        Picasso.with(context)
                .load(Util.getTweetUserReasonableImageUrl(tweet.user.profileImageUrl))
                .placeholder(R.mipmap.ic_placeholder_twitter)
                .into(imageUser);

    }

    private void showTweetPhoto(Tweet displayTweet) {
        MediaEntity entity = Util.getLastPhotoEntity(displayTweet);
        clearMediaBackground();
        if (entity != null) {
            imageTweetPhoto.resetSize();
            imageTweetPhoto.setAspectRatio(Util.getAspectRatio(entity));
            imageTweetPhoto.setVisibility(ImageView.VISIBLE);
            Picasso.with(context)
                    .load(entity.mediaUrl)
                    .into(imageTweetPhoto);
        } else {
            imageTweetPhoto.setVisibility(ImageView.GONE);
        }
    }

    protected void clearMediaBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageTweetPhoto.setBackground(null);
        } else {
            imageTweetPhoto.setBackgroundDrawable(null);
        }
    }

}
