/*------------------------------------------------------------------------------
 *
 *  This file is part of the Chirp SDK for Android.
 *  For full information on usage and licensing, see http://chirp.io/
 *
 *  Copyright © 2011-2016, Asio Ltd.
 *  All rights reserved.
 *
 *----------------------------------------------------------------------------*/

package io.chirp.gemswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import io.chirp.sdk.boundary.ChirpAudioState;
import io.chirp.sdk.model.Chirp;

public class GemView extends ImageView {

    private static final int MAX_CLICK_DURATION = 100;
    private static final int GEM_DIMENSION = 110;
    public static final int GEM_ROTATION = 40;

    protected static final int[] GEM_COLORS = {
            R.drawable.gem_blue,
            R.drawable.gem_green,
            R.drawable.gem_yellow,
            R.drawable.gem_purple,
            R.drawable.gem_red,
            R.drawable.gem_silver
    };

    MainActivity activity;
    RelativeLayout parent;

    int gemWidth;
    int gemHeight;
    int _xDelta;
    int _yDelta;

    private long startTouchTime;

    public GemView(Context context, int x, int y, int color, int rotation) {
        super(context);
        activity = (MainActivity) context;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        gemWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, GEM_DIMENSION, displayMetrics);
        gemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, GEM_DIMENSION, displayMetrics);

        setRotation((float) rotation - GEM_ROTATION / 2);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), GEM_COLORS[color]);
        setImageBitmap(Bitmap.createScaledBitmap(bm, gemWidth, gemHeight, true));
        setTag(color);
        setBackgroundColor(0x0);
        setScaleType(ImageView.ScaleType.FIT_CENTER);

        parent = (RelativeLayout) activity.findViewById(R.id.relativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gemWidth, gemHeight);
        params.leftMargin = x * displayMetrics.widthPixels / 100 - gemWidth / 2;
        params.topMargin = y * displayMetrics.heightPixels / 100 - gemHeight / 2;
        params.rightMargin = -250;
        params.bottomMargin = -250;
        parent.addView(this, params);
        
        Animation grow = AnimationUtils.loadAnimation(getContext(), R.anim.grow);
        startAnimation(grow);
    }

    protected void onClicked() {
        /*------------------------------------------------------------------------------
         * When a gem is tapped, generate a 10-character chirp identifier 
         * that encapsulates the gem's position, colour and rotation.
         *----------------------------------------------------------------------------*/
        if (activity.chirpSDK.getChirpAudioState() == ChirpAudioState.ChirpAudioStateChirping || activity.chirpSDK.getChirpAudioState() == ChirpAudioState.ChirpAudioStateReceiving) {
            return;
        }

        String identifier = getGemIdentifier();
        activity.chirpSDK.chirp(new Chirp(identifier));
        final GemView gemView = this;

        Animation shrink = AnimationUtils.loadAnimation(getContext(), R.anim.shrink);
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gemView.parent.post(new Runnable() {
                    @Override
                    public void run() {
                        gemView.parent.removeView(gemView);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(shrink);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        parent.bringChildToFront(this);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startTouchTime = Calendar.getInstance().getTimeInMillis();
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this
                        .getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                setLayoutParams(layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                long touchDuration = Calendar.getInstance().getTimeInMillis() - startTouchTime;

                if (touchDuration < MAX_CLICK_DURATION) {
                    onClicked();
                    return false;
                }
        }
        return true;
    }

    protected String getGemIdentifier() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int gemWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 156, displayMetrics);
        int gemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, displayMetrics);

        int x = (int) (getX() + gemWidth / 2) * 100 / displayMetrics.widthPixels;
        int y = (int) (getY() + gemHeight / 2) * 100 / displayMetrics.heightPixels;
        int c = (int) getTag();
        int r = (int) getRotation() + GEM_ROTATION / 2;
        Log.d(MainActivity.TAG, String.format("getGemIdentifier: %d - %d - %d - %d", x, y, c, r));
        return String.format("%03d%03d%01d%03d", x, y, c, r);
    }
}
