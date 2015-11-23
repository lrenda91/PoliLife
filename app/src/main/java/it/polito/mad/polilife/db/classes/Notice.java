package it.polito.mad.polilife.db.classes;

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
public class Notice extends ParseObject {

    /**
     * 'Common' keys, e.g. which belong to all notices
     */
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "cost";
    public static final String PROPERTY_TYPE = "type";
    public static final String LOCATION_STRING = "locationName";
    public static final String LOCATION_POINT = "location";
    public static final String TAGS = "keywords";
    public static final String PHOTOS = "photos";
    public static final String OWNER = "owner";
    public static final String DETAILS = "details";

    /**
     * House related notices' keys
     */
    public static final String CONTRACT_TYPE = "contractType";
    public static final String SIZE = "size";
    public static final String AVAILABILITY = "availableFrom";


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

    public static class FilterData implements Serializable {
        public FilterData(){
            minSize = 0; minPrice = 0;
            maxSize = 10000; maxPrice = 10000;  //huge values
        }
        public String title, location, contractType, propertyType;
        public List<String> tags = new LinkedList<>();
        public int minSize, maxSize, minPrice, maxPrice;
        public Double latitude = null, longitude = null;
        public int within = 1;
        public FilterData title(String value){ title = value; return this; }
        public FilterData location(String value){ location = value; return this; }
        public FilterData latitude(Double value){ latitude = value; return this; }
        public FilterData longitude(Double value){ longitude = value; return this; }
        public FilterData within(int value){ within = value; return this; }
        public FilterData contractType(String value){ contractType = value; return this; }
        public FilterData propertyType(String value){ propertyType = value; return this; }
        public FilterData minSize(int value){ minSize = value; return this; }
        public FilterData maxSize(int value){ maxSize = value; return this; }
        public FilterData minPrice(int value){ minPrice = value; return this; }
        public FilterData maxPrice(int value){ maxPrice = value; return this; }
        public FilterData newTag(String value){ tags.add(value); return this; }
        public FilterData removeTag(String value){ tags.remove(value); return this; }
    }


    public String getTitle(){
        return (String) get(TITLE);
    }
    public void setTitle(String value){
        put(TITLE, value);
    }

    public String getDescription(){
        return (String) get(DESCRIPTION);
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

    public String getType(){
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

    public int getCost(){
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