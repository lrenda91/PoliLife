package it.polito.mad.polilife.db.push;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by luigi on 25/01/16.
 */
public class PushManager {

    interface Listener {
        void onPushSent();
        void onPushError(Exception e);
    }

    public static void sendPushNotification(List<String> channels, JSONObject jsonPayload,
                                            final Listener listener){

        List<ParseQuery<ParseUser>> queriesBasedOnSingleChannels = new LinkedList<>();
        for (String ch : channels){
            queriesBasedOnSingleChannels.add(
                    ParseUser.getQuery().whereContainsAll("channels", Arrays.asList(ch))
            );
        }
        /**
         * This query deals with all users who have at least one of the param 'channels' values
         * inside their array named 'channels'
         */
        ParseQuery<ParseUser> users = ParseQuery.or(queriesBasedOnSingleChannels);



        ParsePush push = new ParsePush();
        ParseQuery query = ParseInstallation.getQuery()
                .whereMatchesQuery("user", users);
        push.setQuery(query);
        push.setData(jsonPayload);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    if (listener != null) listener.onPushError(e);
                    return;
                }
                if (listener != null) listener.onPushSent();
            }
        });

    }


}
