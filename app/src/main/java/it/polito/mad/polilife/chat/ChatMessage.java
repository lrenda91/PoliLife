package it.polito.mad.polilife.chat;

/**
 * Created by luigi on 05/01/16.
 */
public class ChatMessage {
    private String username;
    private String message;
    private long timeStamp;

    public ChatMessage(String username, String message, long timeStamp){
        this.username  = username;
        this.message   = message;
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}