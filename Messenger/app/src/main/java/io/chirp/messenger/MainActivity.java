/*------------------------------------------------------------------------------
 *
 *  This file is part of the Chirp SDK for Android.
 *  For full information on usage and licensing, see http://chirp.io/
 *
 *  Copyright © 2011-2016, Asio Ltd.
 *  All rights reserved.
 *
 *----------------------------------------------------------------------------*/

package io.chirp.messenger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.chirp.sdk.CallbackCreate;
import io.chirp.sdk.CallbackRead;
import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.Chirp;
import io.chirp.sdk.model.ChirpError;

public class MainActivity extends AppCompatActivity
{

    private static final int RESULT_REQUEST_RECORD_AUDIO = 0;
    private static final String TAG = "Messenger";

    private MessengerApplication app;
    private ChirpSDK chirpSDK;

    /*------------------------------------------------------------------------------
     * A ChirpSDKListener object responds to events generated by the Chirp
     * decoder, when chirp audio is heard or errors occur.
     *----------------------------------------------------------------------------*/
    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener() {

        /*------------------------------------------------------------------------------
         * onChirpHeard is triggered when a Chirp tone is received.
         * Obtain the chirp's 10-character identifier with `getIdentifier`.
         *----------------------------------------------------------------------------*/
        @Override
        public void onChirpHeard(Chirp chirp)
        {
            Log.d(TAG, "onChirpHeard: " + chirp.getIdentifier());

            /*------------------------------------------------------------------------------
             * As soon as we hear a chirp, query the API for its associated data.
             *----------------------------------------------------------------------------*/
            readChirp(chirp);
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
        public void onChirpError(ChirpError chirpError)
        {
            Log.d(TAG, "onChirpError: " + chirpError.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        app = (MessengerApplication) getApplicationContext();

        ListView listView = (ListView) findViewById(R.id.receiveView);
        listView.setAdapter(app.adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChirpMessage chirpMessage = (ChirpMessage) parent.getItemAtPosition(position);
                chirpSDK.chirp(new Chirp(chirpMessage.getIdentifier()));
            }
        });
    }

    private boolean doWeHaveRecordAudioPermission() {
        /*------------------------------------------------------------------------------
         * Audio permissions are required for Chirp I/O.
         *----------------------------------------------------------------------------*/
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
        if (doWeHaveRecordAudioPermission()) {
            chirpSDK.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*------------------------------------------------------------------------------
         * When backgrounded, stop listening.
         * Otherwise, the application will keep listening for chirps in the
         * background. 
         *----------------------------------------------------------------------------*/
        chirpSDK.stop();
    }


    public void onClick(View view) {
        String sendText = ((TextView) findViewById(R.id.sendText)).getText().toString();
        if (sendText.length() == 0) return;

        ((TextView) findViewById(R.id.sendText)).setText("");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", sendText);
            createChirp(jsonObject, sendText);

        } catch (JSONException e) {
            Log.d(TAG, e.toString());
        }
    }

    private void createChirp(JSONObject jsonObject, final String sendText)
    {
        /*------------------------------------------------------------------------------
         * Create a chirp encapsulating an arbitrary JSON object.
         * The identifier is generated by the API and returned to the application.
         *----------------------------------------------------------------------------*/
        chirpSDK.create(new Chirp(jsonObject), new CallbackCreate()
        {
            /*------------------------------------------------------------------------------
             * The chirp has been created successfully.
             *----------------------------------------------------------------------------*/
            @Override
            public void onCreateResponse(final Chirp chirp)
            {
                /*------------------------------------------------------------------------------
                 * Play the chirp from the device's speaker.
                 *----------------------------------------------------------------------------*/
                chirpSDK.chirp(chirp);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        app.adapter.add(new ChirpMessage(ChirpMessage.Type.SENT, sendText, chirp.getIdentifier()));
                    }
                });
                Log.d(TAG, "onCreateResponse: " + chirp.getIdentifier());
            }

            /*------------------------------------------------------------------------------
             * If a network error occurred whilst creating the chirp, generate an error.
             *----------------------------------------------------------------------------*/
            @Override
            public void onCreateError(ChirpError chirpError)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error creating Chirp", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "onCreateError: " + chirpError.getMessage());
            }
        });
    }

    private void readChirp(Chirp chirp)
    {
        /*------------------------------------------------------------------------------
         * ChirpSDK.read queries the Chirp API for extended data associated with a
         * given chirp. It requires an internet connection.
         *----------------------------------------------------------------------------*/
        chirpSDK.read(chirp, new CallbackRead()
        {
            /*------------------------------------------------------------------------------
             * The associated data is a single JSON structured object of key-value pairs.
             * You can define arbitrary nested data structure within this.
             * Here, we simply retrieve the "text" key.
             *----------------------------------------------------------------------------*/
            @Override
            public void onReadResponse(final Chirp chirp)
            {
                try
                {
                    final String receivedText = (String) chirp.getJsonData().get("text");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            app.adapter.add(new ChirpMessage(ChirpMessage.Type.RECEIVED, receivedText, chirp.getIdentifier()));
                        }
                    });
                } catch (JSONException e) {
                    Log.d(TAG, e.toString());
                }
                Log.d(TAG, "onReadResponse: ");
            }

            /*------------------------------------------------------------------------------
             * If an error occurs contacting the Chirp API, generate an error.
             *----------------------------------------------------------------------------*/
            @Override
            public void onReadError(ChirpError chirpError) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error reading Chirp", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "onReadError: " + chirpError.getMessage());
            }
        });
    }
}
