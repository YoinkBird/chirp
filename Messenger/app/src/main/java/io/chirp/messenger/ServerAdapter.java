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

  // either send to server
  // or 
  // mock the server decision
  /*
  public int send_to_server(float lastLat , float lastLong){
    // Mock decision

  }
  */


}

