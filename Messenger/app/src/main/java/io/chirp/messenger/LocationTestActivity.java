package io.chirp.messenger;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocationTestActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);

    ((Button) findViewById(R.id.location_refresh)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        location_refreshDisplay();
      }
    });
    ((Button) findViewById(R.id.location_forcelocation)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        LocationLibrary.forceLocationUpdate(LocationTestActivity.this);
        Toast.makeText(getApplicationContext(), "Forcing a location update", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    location_onResume();
  }

  public void location_onResume() {
    // cancel any notification we may have received from LocationTestBroadcastReceiver
    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);

    location_refreshDisplay();

    // This demonstrates how to dynamically create a receiver to listen to the location updates.
    // You could also register a receiver in your manifest.
    final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
    registerReceiver(lftBroadcastReceiver, lftIntentFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    location_onPause();
  }

  public void location_onPause() {
    unregisterReceiver(lftBroadcastReceiver);
  }

  private void location_refreshDisplay() {
    location_refreshDisplay(new LocationInfo(this));
  }

  private void location_refreshDisplay(final LocationInfo locationInfo) {
    final View locationTable = findViewById(R.id.location_table);
    final Button buttonShowMap = (Button) findViewById(R.id.location_showmap);
    final Button buttonSendGPS = (Button) findViewById(R.id.location_send_gps);
    final TextView locationTextView = (TextView) findViewById(R.id.location_title);

    if (locationInfo.anyLocationDataReceived()) {
      locationTable.setVisibility(View.VISIBLE);
      ((TextView)findViewById(R.id.location_timestamp)).setText(LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true));
      ((TextView)findViewById(R.id.location_latitude)).setText(Float.toString(locationInfo.lastLat));
      ((TextView)findViewById(R.id.location_longitude)).setText(Float.toString(locationInfo.lastLong));
      ((TextView)findViewById(R.id.location_accuracy)).setText(Integer.toString(locationInfo.lastAccuracy) + "m");
      ((TextView)findViewById(R.id.location_provider)).setText(locationInfo.lastProvider);
      if (locationInfo.hasLatestDataBeenBroadcast()) {
        locationTextView.setText("Latest location has been broadcast");
      }
      else {
        locationTextView.setText("Location broadcast pending (last " + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true) + ")");
      }
      buttonShowMap.setVisibility(View.VISIBLE);
      buttonShowMap.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + locationInfo.lastLat + "," + locationInfo.lastLong + "?q=" + locationInfo.lastLat + "," + locationInfo.lastLong + "(" + locationInfo.lastAccuracy + "m at " + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true) + ")"));
          startActivity(intent);
        }
      });
      buttonSendGPS.setVisibility(View.VISIBLE);
      buttonSendGPS.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          sendLocationToActivity(locationInfo);
        }
      });
    }
    else {
      locationTable.setVisibility(View.GONE);
      buttonShowMap.setVisibility(View.GONE);
      locationTextView.setText("No locations recorded yet");
    }
  }

  private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // extract the location info in the broadcast
      final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
      // refresh the display with it
      location_refreshDisplay(locationInfo);
      // send info to main activity
      sendLocationToActivity(locationInfo);
    }
  };

  private void sendLocationToActivity(final LocationInfo locationInfo){
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    intent.putExtra("LAT", locationInfo.lastLat);
    intent.putExtra("LONG", locationInfo.lastLong);
    startActivity(intent);
  }
}
