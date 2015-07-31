package com.vgarshyn.twittasteroid.core.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Clickable span to open given Url in default system browser
 *
 * Created by v.garshyn on 30.07.15.
 */
public class ClickableUrlSpan extends ClickableSpan {

    private String url;

    public ClickableUrlSpan(String url) {
        super();
        this.url = url;
    }

    @Override
    public void onClick(View view) {
        Context context = view.getContext();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application to handle this request. Please install a Web Browser before.", Toast.LENGTH_LONG).show();
        }
    }
}
