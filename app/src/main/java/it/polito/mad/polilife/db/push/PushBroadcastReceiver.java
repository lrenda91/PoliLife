package it.polito.mad.polilife.db.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import it.polito.mad.polilife.MainActivity;
import it.polito.mad.polilife.PoliLifeApp;
import it.polito.mad.polilife.R;

/**
 * Created by luigi onSelectAppliedJobs 31/12/15.
 */
public class PushBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {

    private static final String PUSH_DATA_BUNDLE_KEY = "com.parse.Data";

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (PoliLifeApp.HomeActivityIsResumed()){
            return;
        }
        if (!intent.hasExtra(PUSH_DATA_BUNDLE_KEY)){
            return;
        }

        String title = context.getString(R.string.app_name);
        int appIcon = R.mipmap.ic_launcher;
        try{
            String jsonData = intent.getStringExtra(PUSH_DATA_BUNDLE_KEY);
            JSONObject pushData = new JSONObject(jsonData);
            if (JSONFactory.isChatMessage(pushData)){
                String sub = context.getString(R.string.chat_push_subtitle);
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("json", jsonData);
                showNotification(context, title, sub, appIcon, i);
            }
            else if (JSONFactory.isDidacticalMessage(pushData)){
                String sub = context.getString(R.string.didactical_push_subtitle);
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("json", jsonData);
                showNotification(context, title, sub, appIcon, i);
            }
        }
        catch (JSONException ignore){}

    }


    private static void showNotification(Context context, String title, String subtitle,
                                         int iconResource, Intent intent){
        PendingIntent pIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setSmallIcon(iconResource)    // Set Icon
                .setTicker(title)         // Set Ticker Message
                .setContentTitle(title)     // Set Title
                .setContentText(subtitle)    // Set Text
                //.addAction(R.drawable.ic_logout, "Action Button", pIntent) // Add an Action Button
                .setContentIntent(pIntent)  // Set PendingIntent into Notification
                .setAutoCancel(true);       // Dismiss Notification

        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());
    }


}
