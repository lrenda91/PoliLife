package it.polito.mad.polilife.db.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Message")
public class Message extends ParseObject {

    static final String SENDER = "Addresser";
    static final String RECEIVER = "Addresee";
    static final String CONTENT = "Content";

    public Message() {

    }

    public StudentInfo getSender(){ return (StudentInfo) get(SENDER); }
    public void setSender(StudentInfo value){ put(SENDER, value); }

    public ParseObject getReceiver(){ return (ParseObject) get(RECEIVER); }
    public void setReceiver(ParseObject value){ put(RECEIVER, value); }

    public String getContent(){ return (String) get(CONTENT); }
    public void setContent(String value){ put(CONTENT, value); }

    public Date getDeliveryTime(){ return getCreatedAt(); }

}