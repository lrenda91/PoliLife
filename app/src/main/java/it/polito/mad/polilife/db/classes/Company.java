package it.polito.mad.polilife.db.classes;

/**
 * Created by luigi on 07/05/15.
 */
import com.parse.*;

import java.util.Collection;
import java.util.List;

@ParseClassName("Company")
public class Company extends ParseObject {

    private static final String NAME = "companyName";
    private static final String FIELDS = "fields";
    private static final String ADDRESS= "address";
    private static final String COUNTRY = "country";
    private static final String CITY= "city";
    private static final String CONTACT_PHONE = "contactPhone";
    private static final String WEB_PAGE = "webPage";
    private static final String MISSION = "mission";
    private static final String POSITIONS = "positions";


    public List<String> getFieldsOfWork(){
        return (List<String>) get(FIELDS);
    }

    public void setFieldsOfWork(List<String> fieldsOfWork){
        put(FIELDS, fieldsOfWork);
    }


    public List<Position> getPositions(){
        return (List<Position>) get(POSITIONS);
    }

    public void setPositions(List<Position> positions){
        put(POSITIONS, positions);
    }

    public String getWebPage(){
        return (String) get(WEB_PAGE);
    }

    public void setWebPage(String webPage){
        put(WEB_PAGE, webPage);
    }
    public String getMission(){
        return (String) get(MISSION);
    }

    public void setMission(String mission){
        put(MISSION, mission);
    }
    public String getContactPhone(){
        return (String) get(CONTACT_PHONE);
    }

    public void setContactPhone(String contactName){
        put(CONTACT_PHONE, contactName);
    }
    public String getName(){
        return (String) get(NAME);
    }

    public void setName(String name){
        put(NAME, name);
    }

    public String getCity(){ return (String) get(CITY);  }
    public void setCity(String value) {   put(CITY, value);  }

    public String getAddress(){  return (String) get(ADDRESS);  }
    public void setAddress(String value) {   put(ADDRESS, value);  }

    public String getCountry(){
        return (String) get(COUNTRY);
    }
    public void setCountry(String value){
        put(COUNTRY, value);
    }

    public void addPosition(ParseRelation<Position> relationPositions, Position value){
        relationPositions.add(value);
    }

    public ParseRelation<Position> getRelationPositions() {
        return getRelation(POSITIONS);
    }

    public void addFieldsOfWork(Collection<String> fields){
        addAllUnique(FIELDS, fields);
    }

}