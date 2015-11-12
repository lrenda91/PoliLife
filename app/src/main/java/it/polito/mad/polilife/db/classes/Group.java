package it.polito.mad.polilife.db.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Group")
public class Group extends ParseObject {

    static final String ADMIN = "Admin";
    static final String SUBSCRIBERS = "Subscribers";

    public Group() {

    }

    public StudentInfo getAdministrator(){ return (StudentInfo) get(ADMIN); }
    public void setAdministrator(StudentInfo admin){ put(ADMIN, admin); }

    public ParseRelation<StudentInfo> getSubscribers(){
        return getRelation(SUBSCRIBERS);
    }
    public void subscribe(StudentInfo value){
        getRelation(SUBSCRIBERS).add(value);
    }
    public void unsubscribe(StudentInfo value){
        getRelation(SUBSCRIBERS).remove(value);
    }

}