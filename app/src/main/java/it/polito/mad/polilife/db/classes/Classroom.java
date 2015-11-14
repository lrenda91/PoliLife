package it.polito.mad.polilife.db.classes;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Classroom")
public class Classroom extends ParseObject {

    public static final String NAME = "name";
    static final String LOCATION = "location";
    static final String SITE = "site";
    static final String FLOOR = "floor";
    static final String DETAILS = "details";

    public Classroom() {

    }

    public String getName() {
        return (String) get(NAME);
    }
    public void setName(String value){
        put(NAME, value);
    }

    public ParseGeoPoint getLocation() {
        return (ParseGeoPoint) get(LOCATION);
    }
    public void setLocation(double lat, double lng) { put(LOCATION, new ParseGeoPoint(lat,lng)); }
    public void setLocation(ParseGeoPoint value){
        put(LOCATION, value);
    }

    public String getSite() {
        return (String) get(SITE);
    }
    public void setSite(String value){
        put(SITE, value);
    }

    public String getFloor() {
        return (String) get(FLOOR);
    }
    public void setFloor(String value){
        put(FLOOR, value);
    }

    public String getDetails() {
        return (String) get(DETAILS);
    }
    public void setDetails(String value){
        put(DETAILS, value);
    }


}