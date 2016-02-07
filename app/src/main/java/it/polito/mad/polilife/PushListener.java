package it.polito.mad.polilife;

import org.json.JSONObject;

/**
 * Created by luigi on 04/02/16.
 */
public interface PushListener {

    void onPushReceived(JSONObject message);

}
