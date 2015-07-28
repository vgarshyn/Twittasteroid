package com.vgarshyn.twittasteroid.core.ui;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by v.garshyn on 28.07.2015.
 */
public class LogoAnimation extends AnimationSet {

    private static final int DURATION_ZOOM_IN = 300;
    private static final int DURATION_ZOOM_OUT = 500;

    private static float SCALE_ZOOM_IN = 0.5f;
    private static float SCALE_ZOOM_OUT = 18.0f;

    public LogoAnimation(Context context) {
        super(true);
        init(context);
    }

    private void init(Context context) {
        ScaleAnimation zoomInAnimation = new ScaleAnimation(1.0f, SCALE_ZOOM_IN, 1.0f, SCALE_ZOOM_IN, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomInAnimation.setInterpolator(new HesitateInterpolator());
        zoomInAnimation.setDuration(DURATION_ZOOM_IN);
        zoomInAnimation.setFillAfter(true);

        ScaleAnimation zoomOutAnimation = new ScaleAnimation(SCALE_ZOOM_IN, SCALE_ZOOM_OUT, SCALE_ZOOM_IN, SCALE_ZOOM_OUT, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomOutAnimation.setInterpolator(context, android.R.anim.overshoot_interpolator);
        zoomOutAnimation.setDuration(DURATION_ZOOM_OUT);
        zoomOutAnimation.setFillAfter(false);

        addAnimation(zoomInAnimation);
        addAnimation(zoomOutAnimation);
    }

    public static abstract class SimpleAnimationListener implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            //Stub
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            //Stub
        }
    }

    class HesitateInterpolator implements Interpolator {

        public float getInterpolation(float t) {
            float x = 2.0f * t - 1.0f;
            return 0.5f * (x * x * x + 1.0f);
        }
    }
}
