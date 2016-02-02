package it.polito.mad.polilife.chat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luigi onSelectAppliedJobs 05/01/16.
 */
public class ChatMessage {

    public final String senderUsername;
    public final String message;
    public final long timeStamp;

    public String senderCompleteName = "";
    public byte[] photoBytes = null;

    public ChatMessage(String senderUsername, String message, long timeStamp){
        this.senderUsername = senderUsername;
        this.message   = message;
        this.timeStamp = timeStamp;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sender", senderUsername);
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
        return "["+ senderUsername +","+message+"]";
    }
}