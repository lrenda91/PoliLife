package it.polito.mad.polilife.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by luigi on 31/12/15.
 */
public class ParsePushBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Push received", Toast.LENGTH_LONG).show();
    }
}
