package it.polito.mad.polilife.db.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Channel")
public class ChatChannel extends ParseObject {

    static final String ADMIN = "Admin";
    static final String SUBSCRIBERS = "Subscribers";

    public ChatChannel() {

    }

    public ParseUser getAdministrator(){ return (ParseUser) get(ADMIN); }
    public void setAdministrator(ParseUser admin){ put(ADMIN, admin); }

    public ParseRelation<ParseUser> getSubscribers(){
        return getRelation(SUBSCRIBERS);
    }
    public void subscribe(ParseUser value){
        getRelation(SUBSCRIBERS).add(value);
    }
    public void unsubscribe(ParseUser value){
        getRelation(SUBSCRIBERS).remove(value);
    }

}