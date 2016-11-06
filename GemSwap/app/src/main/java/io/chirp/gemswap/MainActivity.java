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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.Chirp;
import io.chirp.sdk.model.ChirpError;

public class MainActivity extends Activity {

    private static final int RESULT_REQUEST_RECORD_AUDIO = 0;
    public static final String TAG = "GemSwap";
    private static final int NUM_GEMS = 8;

    private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
    private int soundSuccess;

    public ChirpSDK chirpSDK;

    /*------------------------------------------------------------------------------
     * A ChirpSDKListener responds to events and errors generated by the
     * Chirp SDK.
     *----------------------------------------------------------------------------*/
    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener()
    {
        /*------------------------------------------------------------------------------
         * onChirpHeard is triggered when a Chirp tone is received.
         * Obtain the chirp's 10-character identifier with `getIdentifier`.
         *----------------------------------------------------------------------------*/
        @Override
        public void onChirpHeard(Chirp chirp)
        {
            /*------------------------------------------------------------------------------
             * We're encoding the properties of each gem within the identifier:
             * Position, orientation, and colour.
             *
             * Create and display a new gem with these properties.
             *----------------------------------------------------------------------------*/
            final String gemId = chirp.getIdentifier();
            Log.d(TAG, "Chirp heard: " + gemId);

            final int[] gem;
            try {
                gem = decodeGemId(gemId);
                soundPool.play(soundSuccess, 0.7f, 0.7f, 1, 0, 1f);
            } catch (NumberFormatException e) {
                Log.d(TAG, "Not a compatible Identifier");
                return;
            }
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GemView(MainActivity.this, gem[0], gem[1], gem[2], gem[3]);
                    }
                });
        }

        /*------------------------------------------------------------------------------
         * onChirpHearStarted is triggered when the beginning of Chirp tone is heard
         *----------------------------------------------------------------------------*/
        @Override
        public void onChirpHearStarted() {
            Log.d(TAG, "Chirp Hear Started");
        }

        /*------------------------------------------------------------------------------
        * onChirpHearFailed is triggered when the beginning of Chirp tone is heard but it
        * subsequently fails to decode the identifier
        *----------------------------------------------------------------------------*/
        @Override
        public void onChirpHearFailed() {
            Log.d(TAG, "Chirp Hear Failed");
        }

        /*------------------------------------------------------------------------------
         * onChirpError is triggered when an error occurs -- for example,
         * authentication failure or muted device.
         *
         * See the documentation on ChirpError for possible error codes.
         *----------------------------------------------------------------------------*/
        @Override
        public void onChirpError(ChirpError chirpError) {
            Log.d(TAG, "Identifier received error: " + chirpError.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundSuccess = soundPool.load(this, R.raw.beep_positive, 1);

        /*------------------------------------------------------------------------------
         * A ChirpSDK object wraps up the general SDK functionality.
         * Pass it your app credentials to authenticate.
         *
         * To register for credentials, please visit https://admin.chirp.io/
         *----------------------------------------------------------------------------*/
        chirpSDK = new ChirpSDK(this, getString(R.string.chirp_app_key), getString(R.string.chirp_app_secret));
        chirpSDK.setListener(chirpSDKListener);

        ((TextView) findViewById(R.id.versionInfo)).setText(String.format(
                "%s: %s - %s\nChirpSDK: %s", getString(R.string.app_name), BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME,
                chirpSDK.getVersion()));

        spawnGems(NUM_GEMS);
    }

    private boolean doWeHaveRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*------------------------------------------------------------------------------
         * When foregrounded, start audio processing with chirpSDK.start().
         * This is required to send or receive chirps.
         *----------------------------------------------------------------------------*/
        if (doWeHaveRecordAudioPermission())
        {
            chirpSDK.start();
        }
    }

    @Override
    protected void onPause() {
        /*------------------------------------------------------------------------------
         * When backgrounded, stop listening.
         * Otherwise, the application will keep listening for chirps in the
         * background. 
         *----------------------------------------------------------------------------*/
        super.onPause();
        chirpSDK.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Audio permission granted");
                }
                return;
            }
        }
    }

    private void spawnGems(int n) {
        for (int i = 0; i < n; i++) {
            Random r = new Random();
            new GemView(this, r.nextInt(101), r.nextInt(101), r.nextInt(6), r.nextInt(GemView.GEM_ROTATION + 1));
        }
    }

    protected int[] decodeGemId(String identifier) {
        int gemId = Integer.parseInt(identifier);
        int[] result = new int[4];
        result[0] = Math.min(100, gemId / 10000000);
        result[1] = Math.min(100, gemId % 10000000 / 10000);
        result[2] = Math.min(5, gemId % 10000 / 1000);
        result[3] = Math.min(GemView.GEM_ROTATION, gemId % 1000);
        Log.d(TAG, String.format("Decoded gem: %d,%d,%d,%d", result[0], result[1], result[2], result[3]));
        return result;
    }

    public void onBackgroundClick(View view) {
        if (((RelativeLayout) view).getChildCount() == 1) {
            spawnGems(NUM_GEMS);
        }
    }
}
