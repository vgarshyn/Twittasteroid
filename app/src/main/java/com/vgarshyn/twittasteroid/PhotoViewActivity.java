package com.vgarshyn.twittasteroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by v.garshyn on 26.07.15.
 */
public class PhotoViewActivity extends AppCompatActivity {
    public static final String EXTRA_PHOTO_URL = "extra.photo_url";
    private static final String TAG = PhotoViewActivity.class.getSimpleName();
    private static final int REQUEST_DIRECTORY = 2707;

    private PhotoView mPhotoView;
    private ProgressBar mProgressBar;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_PHOTO_URL))) {
            finish();
        } else {
            mImageUrl = getIntent().getStringExtra(EXTRA_PHOTO_URL);
        }
        setContentView(R.layout.activity_photo_view);
        mPhotoView = (PhotoView) findViewById(R.id.photoViewer);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.with(this).load(mImageUrl).into(mPhotoView, new Callback() {
            @Override
            public void onSuccess() {
                setVisibility();
            }

            @Override
            public void onError() {
                setVisibility();
                //todo error stub
            }

            void setVisibility() {
                mProgressBar.setVisibility(View.INVISIBLE);
                mPhotoView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                String directory = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                downloadImage(directory);
            } else {
                // Nothing selected
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            pickTargetDirectory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //https://github.com/passy/Android-DirectoryChooser
    private void pickTargetDirectory() {
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);
        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME, "TwitterDownloads");

        try {
            startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadImage(final String directory) {

        Target target = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
                return;
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                try {
                    String fileName = mImageUrl.substring(mImageUrl.lastIndexOf('/') + 1, mImageUrl.length());
                    File file = new File(directory, fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    FileOutputStream ostream = new FileOutputStream(file);
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

        Picasso.with(this)
                .load(mImageUrl)
                .into(target);
    }
}
