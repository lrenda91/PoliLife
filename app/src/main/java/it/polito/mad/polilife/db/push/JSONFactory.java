package it.polito.mad.polilife.db.push;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luigi on 30/01/16.
 */
public class JSONFactory {

    private JSONFactory(){}

    public static JSONObject createChatMessage() throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("feature", "chat");
        return msg;
    }

    public static JSONObject createChatSetupMessage(String channel, List<String> subscribers)
            throws JSONException {
        JSONObject msg = createChatMessage();
        msg.put("type", "setup");
        msg.put("group", channel);
        msg.put("receiverIDs", new JSONArray(subscribers));
        return msg;
    }

    public static boolean isChatMessage(JSONObject obj) throws JSONException {
        return obj.has("feature") && obj.get("feature").equals("chat");
    }

    public static boolean isDidacticalMessage(JSONObject obj) throws JSONException {
        return obj.has("feature") && obj.get("feature").equals("didactical");
    }

}
