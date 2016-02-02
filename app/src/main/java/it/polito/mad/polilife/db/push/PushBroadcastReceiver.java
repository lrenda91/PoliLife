package it.polito.mad.polilife.db.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import it.polito.mad.polilife.HomeActivity;
import it.polito.mad.polilife.MainActivity;
import it.polito.mad.polilife.PoliLifeApp;
import it.polito.mad.polilife.R;

/**
 * Created by luigi onSelectAppliedJobs 31/12/15.
 */
public class PushBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {

    public static final String HOME_ACTIVITY_INTENT_ACTION = "it.polito.mad.polilife.bcast";

    private static final String PUSH_DATA_EXTRA_KEY = "com.parse.Data";

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (!intent.hasExtra(PUSH_DATA_EXTRA_KEY)){
            return;
        }
        String jsonData = intent.getStringExtra(PUSH_DATA_EXTRA_KEY);
        String title = context.getString(R.string.app_name);
        String sub = "";
        int appIcon = R.mipmap.ic_launcher2;
        Intent notificationIntent = new Intent();

        if (PoliLifeApp.HomeActivityIsResumed()){
            Intent i = new Intent(HOME_ACTIVITY_INTENT_ACTION);
            i.putExtra("json", jsonData);
            context.sendBroadcast(i);
        }
        else {
            notificationIntent = new Intent(context, HomeActivity.class);
            notificationIntent.putExtra("json", jsonData);

            try {
                JSONObject pushData = new JSONObject(jsonData);
                if (JSONFactory.isChatMessage(pushData)) {
                    sub = context.getString(R.string.chat_push_subtitle);
                } else if (JSONFactory.isDidacticalMessage(pushData)) {
                    sub = context.getString(R.string.didactical_push_subtitle);
                } else if (JSONFactory.isJobMessage(pushData)) {
                    sub = context.getString(R.string.job_push_subtitle);
                }
            } catch (JSONException ignore) {
            }
        }

        showNotification(context, title, sub, appIcon, notificationIntent);
    }


    private static void showNotification(Context context, String title, String subtitle,
                                         int iconResource, Intent intent){
        PendingIntent pIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setSmallIcon(iconResource)    // Set Icon
                .setTicker(title)         // Set Ticker message
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
