package io.chirp.messenger;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationTestBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LocationBroadcastReceiver", "onReceive: received location update");
        
        final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
        
        // The broadcast has woken up your app, and so you could do anything now - 
        // perhaps send the location to a server, or refresh an on-screen widget.
        // We're gonna create a notification.
        

        Intent contentIntent = new Intent(context, LocationTestActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Construct the notification.
        int icon = R.drawable.notification;
        long time = System.currentTimeMillis();
        String text1 = "Locaton updated " + locationInfo.getTimestampAgeInSeconds() + " seconds ago";
        String title = "Location update broadcast received";
        String text2 = "Timestamped " + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true);
        /*
        Notification notification = new Notification(
            icon,
            text1,
            time);
        notification.setLatestEventInfo(context, title, text2, contentPendingIntent);
        */

        //   src: http://stackoverflow.com/a/16857681
        //   src: http://stackoverflow.com/a/33085754
        //   https://developer.android.com/sdk/api_diff/23/changes/android.app.Notification.html#android.app.Notification.setLatestEventInfo_removed%28android.content.Context,%20java.lang.CharSequence,%20java.lang.CharSequence,%20android.app.PendingIntent%29
        //   To Level: 	23 | From Level: 	22
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentIntent(contentPendingIntent)
          .setSmallIcon(icon).setTicker(text2).setWhen(time)
          .setAutoCancel(true).setContentTitle(title)
          .setContentText(text1).build();

        // Trigger the notification.
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1234, notification);
        
    }
}

