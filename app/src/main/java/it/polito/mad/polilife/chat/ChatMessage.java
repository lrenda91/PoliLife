package it.polito.mad.polilife.chat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luigi on 05/01/16.
 */
public class ChatMessage {

    String username;
    String message;
    long timeStamp;

    public ChatMessage(String username, String message, long timeStamp){
        this.username  = username;
        this.message   = message;
        this.timeStamp = timeStamp;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sender", username);
        obj.put("content", message);
        obj.put("ts", timeStamp);
        return obj;
    }

    public static ChatMessage fromJSON(JSONObject obj) throws JSONException {
        return new ChatMessage(
            obj.getString("sender"),
                obj.getString("content"),
                obj.getLong("ts")
        );
    }

    @Override
    public String toString() {
        return "["+username+","+message+","+timeStamp+"]";
    }
}