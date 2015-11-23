package it.polito.mad.polilife.db.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Classroom")
public class Classroom extends ParseObject implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Classroom createFromParcel(Parcel in) {
            return new Classroom(in);
        }
        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };

    public Classroom(Parcel in){
        String name = in.readString();
        String loc = in.readString();
        String floor = in.readString();
        String details = in.readString();
        if (name != null) put(NAME, name);
        if (loc != null) put(LOCATION, loc);
        if (floor != null) put(FLOOR, floor);
        if (details != null) put(DETAILS, details);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString((String) get(NAME));
        dest.writeString((String) get(LOCATION));
        dest.writeString((String) get(FLOOR));
        dest.writeString((String) get(DETAILS));
    }

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