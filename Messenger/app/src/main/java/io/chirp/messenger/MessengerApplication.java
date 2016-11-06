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

import android.app.Application;

import java.util.ArrayList;

public class MessengerApplication extends Application {
    private ArrayList<ChirpMessage> arrayList = new ArrayList<ChirpMessage>();
    public MessageArrayAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        adapter = new MessageArrayAdapter(this, android.R.layout.list_content, arrayList);
    }
}
