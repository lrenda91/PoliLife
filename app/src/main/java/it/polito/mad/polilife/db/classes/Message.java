package it.polito.mad.polilife.db.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;


/**
 * Created by luigi onSelectAppliedJobs 29/05/15.
 */
@ParseClassName("Message")
public class Message extends ParseObject implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public Message(Parcel in){
        String content = in.readString();
        if (content != null) put(CONTENT, content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString((String) get(CONTENT));
    }


    public Message() {

    }

    static final String SENDER = "Addresser";
    static final String RECEIVER = "Addresee";
    static final String CONTENT = "Content";

    public StudentInfo getSender(){ return (StudentInfo) get(SENDER); }
    public void setSender(StudentInfo value){ put(SENDER, value); }

    public ParseObject getReceiver(){ return (ParseObject) get(RECEIVER); }
    public void setReceiver(ParseObject value){ put(RECEIVER, value); }

    public String getContent(){ return (String) get(CONTENT); }
    public void setContent(String value){ put(CONTENT, value); }

    public Date getDeliveryTime(){ return getCreatedAt(); }


}