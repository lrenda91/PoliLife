package it.polito.mad.polilife.db.classes;

/**
 * Created by luigi onSelectAppliedJobs 07/05/15.
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
    private static final String ABOUT = "about";
    private static final String POSITIONS = "positions";
    public static final String LOGO = "logo";


    public List<String> getFieldsOfWork(){
        return (List<String>) get(FIELDS);
    }

    public void setFieldsOfWork(List<String> fieldsOfWork){
        put(FIELDS, fieldsOfWork);
    }


    public List<Job> getPositions(){
        return (List<Job>) get(POSITIONS);
    }

    public void setPositions(List<Job> jobs){
        put(POSITIONS, jobs);
    }

    public String getWebPage(){
        return (String) get(WEB_PAGE);
    }

    public void setWebPage(String webPage){
        put(WEB_PAGE, webPage);
    }
    public String getAbout(){
        return (String) get(ABOUT);
    }
    public void setAbout(String value){
        put(ABOUT, value);
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

    public void addPosition(ParseRelation<Job> relationPositions, Job value){
        relationPositions.add(value);
    }

    public ParseRelation<Job> getRelationPositions() {
        return getRelation(POSITIONS);
    }

    public void addFieldsOfWork(Collection<String> fields){
        addAllUnique(FIELDS, fields);
    }

    public ParseFile getLogo(){ return (ParseFile) get(LOGO); }
    public void setLogo(ParseFile value){ put(LOGO, value); }

}