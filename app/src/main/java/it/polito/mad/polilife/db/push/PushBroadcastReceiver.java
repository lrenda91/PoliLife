package it.polito.mad.polilife.db.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import it.polito.mad.polilife.MainActivity;
import it.polito.mad.polilife.R;

/**
 * Created by luigi onSelectAppliedJobs 31/12/15.
 */
public class PushBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");
        String message = "message";
        String strtitle = "title";
        // Open NotificationView Class on Notification Click
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("json", jsonData);
        // Send data to NotificationView Class
        i.putExtra("title", strtitle);
        i.putExtra("text", message);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_add)
                        // Set Ticker Message
                .setTicker(message)
                        // Set Title
                .setContentTitle(context.getString(R.string.no_start_date))
                        // Set Text
                .setContentText(message)
                        // Add an Action Button below Notification
                .addAction(R.drawable.ic_logout, "Action Button", pIntent)
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
    }
}
