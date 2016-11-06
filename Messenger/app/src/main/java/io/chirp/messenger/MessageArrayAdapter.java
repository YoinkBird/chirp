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

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageArrayAdapter extends ArrayAdapter<ChirpMessage> {
    public MessageArrayAdapter(Context context, int layoutId, ArrayList<ChirpMessage> arrayList) {
        super(context, layoutId, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;

        if (convertView == null) {
            row = layoutInflater.inflate(R.layout.chirp_message, null);
        }
        else {
            row = convertView;
        }
        ChirpMessage chirpMessage = getItem(position);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (chirpMessage.getType()) {
            case SENT:
                layoutParams.setMargins(150, 0, 0, 0);
                layoutParams.gravity = Gravity.RIGHT;
                break;
            case RECEIVED:
                layoutParams.setMargins(0, 0, 150, 0);
                layoutParams.gravity = Gravity.LEFT;
                break;
        }

        TextView textView = (TextView) row.findViewById(R.id.messageTv);
        textView.setLayoutParams(layoutParams);
        textView.setText(chirpMessage.getMessage());

        return row;
    }

}
