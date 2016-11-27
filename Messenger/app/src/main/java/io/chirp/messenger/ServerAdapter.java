package io.chirp.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Server Adapter
// two purposes:
// 1. abstract away the server interface
// 2. mock the server activity
public class ServerAdapter {
  Context mContextServerAdapter = null;
  private static final String TAG = "ServerActivity";

  public ServerAdapter(Context context) {
    mContextServerAdapter = context;
    // TODO Auto-generated constructor stub
  }
  public ServerAdapter() {
    // TODO Auto-generated constructor stub
  }

  // 0 : not going to make it
  // 1 : good trajectory
  // 2 : already within office GPS radius
  public static int is_client_punctual(float lastLat , float lastLong){
    // return code
    int rc = 0;
    // MOCK: hard-code
    // tolerance - "on-time" range
    int tolerance = 5;
    // boundary - border of office
    int boundary  = 1;
    // lat+long of office
    int office_lat  = 50;
    int office_long = 50;

    // 0 : not going to make it - out of tolerance
    if(lastLat < ( office_lat - tolerance) || 
       lastLat > ( office_lat + tolerance) ){
      rc = 0;
    }
    // 1 : good trajectory - within tolerance
    if(lastLat > ( office_lat - tolerance) || 
       lastLat < ( office_lat + tolerance) ){
      rc = 1;
    }
    // 2 : already within office range
    if(lastLat > ( office_lat - boundary) || 
       lastLat < ( office_lat + boundary) ){
      rc = 2;
    }

    return rc;
  }
  // either send to server
  // or 
  // mock the server decision
  /*
  public int send_to_server(float lastLat , float lastLong){
    // Mock decision

  }
  */


}

