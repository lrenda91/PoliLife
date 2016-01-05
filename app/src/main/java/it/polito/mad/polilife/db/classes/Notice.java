package it.polito.mad.polilife.db.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Models a Notice, but it could deal with houses OR book/notes so we must discriminate it
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Notice")
public class Notice extends ParseObject implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };

    public Notice(){

    }

    public Notice(Parcel in){
        String type = in.readString();
        String title = in.readString();
        String description = in.readString();
        int price = in.readInt();
        String locStr = in.readString();
        boolean[] b = new boolean[1];
        in.readBooleanArray(b);
        ParseGeoPoint pgp = null;
        if (b[0]){
            pgp = new ParseGeoPoint(in.readDouble(), in.readDouble());
        }
        List<String> tags = new LinkedList<>();
        in.readStringList(tags);

        if (type != null) put(TYPE, type);
        if (title != null) setTitle(title);
        if (description != null) setDescription(description);
        setCost(price);
        if (locStr != null) setLocationName(locStr);
        if (pgp != null) setLocationPoint(pgp);
        addTags(tags);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getType());
        dest.writeString(getTitle());
        dest.writeString(getDescription());
        dest.writeInt(getPrice());
        dest.writeString(getLocationName());
        boolean hasPositionPoint = getLocationPoint() != null;
        dest.writeBooleanArray(new boolean[]{hasPositionPoint});
        if (hasPositionPoint){
            dest.writeDouble(getLocationPoint().getLatitude());
            dest.writeDouble(getLocationPoint().getLongitude());
        }
        dest.writeStringList(getTags());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 'Common' keys, e.g. which belong to all notices
     */
    public static final String TYPE = "type";
    public static final String TITLE = "name";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "cost";
    public static final String LOCATION_STRING = "locationName";
    public static final String LOCATION_POINT = "location";
    public static final String TAGS = "keywords";
    public static final String PHOTOS = "photos";
    public static final String OWNER = "owner";
    public static final String PUBLICATED_AT = "createdAt";


    /**
     * House related notices' keys
     */
    public static final String PROPERTY_TYPE = "property";
    public static final String CONTRACT_TYPE = "contract";
    public static final String SIZE = "size";
    public static final String AVAILABILITY = "availableFrom";

    private static final String HOME_TYPE = "home";
    private static final String BOOK_TYPE = "book";

    public static class SortParam {
        public String key;
        public boolean consider;
        public SortParam(String t, boolean ch){ key = t; consider = ch; }
        @Override
        public String toString() {
            return "["+key+":"+consider+"]";
        }
    }

    public static List<SortParam> getDefaultSortCriteria(){
        List<SortParam> res = new LinkedList<>();
        res.add(new SortParam(PRICE, true));
        res.add(new SortParam(AVAILABILITY, true));
        res.add(new SortParam(SIZE, true));
        return res;
    }

    public static class Filter implements Serializable {
        public Filter(){
            minSize = 0; minPrice = 0;
            maxSize = 10000; maxPrice = 10000;  //huge values
        }
        public String type, title, location, contractType, propertyType;
        public List<String> tags = new LinkedList<>();
        public int minSize, maxSize, minPrice, maxPrice;
        public Double latitude = null, longitude = null;
        public int within = 1;
        public int daysAgo = -1;
        public Filter homeType(){ type = HOME_TYPE; return this; }
        public Filter bookType(){ type = BOOK_TYPE; return this; }
        public Filter title(String value){ title = value; return this; }
        public Filter location(String value){ location = value; return this; }
        public Filter latitude(Double value){ latitude = value; return this; }
        public Filter longitude(Double value){ longitude = value; return this; }
        public Filter within(int value){ within = value; return this; }
        public Filter contractType(String value){ contractType = value; return this; }
        public Filter propertyType(String value){ propertyType = value; return this; }
        public Filter minSize(int value){ minSize = value; return this; }
        public Filter maxSize(int value){ maxSize = value; return this; }
        public Filter minPrice(int value){ minPrice = value; return this; }
        public Filter maxPrice(int value){ maxPrice = value; return this; }
        public Filter newTag(String value){ tags.add(value); return this; }
        public Filter removeTag(String value){ tags.remove(value); return this; }
    }

    public String getType(){ return getString(TYPE); }
    public void setHomeType(){ put(TYPE, HOME_TYPE); }
    public void setBookType(){ put(TYPE, BOOK_TYPE); }

    public String getTitle(){
        return getString(TITLE);
    }
    public void setTitle(String value){
        put(TITLE, value);
    }

    public String getDescription(){
        return getString(DESCRIPTION);
    }
    public void setDescription(String value){
        put(DESCRIPTION, value);
    }

    public ParseGeoPoint getLocationPoint(){
        return (ParseGeoPoint) get(LOCATION_POINT);
    }
    public void setLocationPoint(ParseGeoPoint value){
        put(LOCATION_POINT, value);
    }

    public String getLocationName(){ return (String) get(LOCATION_STRING); }
    public void setLocationName(String value){ put(LOCATION_STRING, value); }

    public String getPropertyType(){
        return (String) get(PROPERTY_TYPE);
    }
    public void setPropertyType(String value){
        put(PROPERTY_TYPE, value);
    }

    public int getSize(){
        return (int) get(SIZE);
    }
    public void setSize(int value){
        put(SIZE, value);
    }

    public String getContractType(){
        return (String) get(CONTRACT_TYPE);
    }
    public void setContractType(String value){
        put(CONTRACT_TYPE, value);
    }

    public Date getPublishedAt(){
        return getCreatedAt();
    }

    public Date getAvailableFrom(){
        return (Date) get(AVAILABILITY);
    }
    public void setAvailableFrom(Date value){
        put(AVAILABILITY, value);
    }

    public int getPrice(){
        return (int) get(PRICE);
    }
    public void setCost(int value){
        put(PRICE, value);
    }

    public List<String> getTags(){
        return (List<String>) get(TAGS);
    }
    public void addTags(Collection<String> values){
        addAllUnique(TAGS, values);
    }
    public void removeTags(Collection<String> values){ removeAll(TAGS, values); }

    public List<ParseFile> getPhotos(){
        return (List<ParseFile>) get(PHOTOS);
    }
    public void addPhotos(Collection<ParseFile> values){
        addAllUnique(PHOTOS, values);
    }
    public void removePhotos(Collection<ParseFile> values){ removeAll(PHOTOS, values); }

    public ParseUser getOwner(){ return (ParseUser) get(OWNER); }
    public void setOwner(ParseUser value){ put(OWNER, value); }

    private int reports = 0;
    public int getReports(){return reports;}
    public void increaseReports(){reports++;}
    public void decreaseReports(){reports--;}

}