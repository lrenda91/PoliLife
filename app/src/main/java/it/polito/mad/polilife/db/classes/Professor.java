package it.polito.mad.polilife.db.classes;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Professor")
public class Professor extends ParseObject {

    static final String NUMBER = "number";
    static final String NAME = "name";
    static final String EMAIL = "email";
    static final String PHONE = "phone";
    static final String OFFICE = "office";

    public Professor() {

    }

    public String getName() {
        return (String) get(NAME);
    }
    public void setName(String value){
        put(NAME, value);
    }

    public String getIDNumber() {
        return (String) get(NUMBER);
    }
    public void setIDNumber(String value){
        put(NUMBER, value);
    }

    public String getEmail() {
        return (String) get(EMAIL);
    }
    public void setEmail(String value){
        put(EMAIL, value);
    }

    public String getPhone() {
        return (String) get(PHONE);
    }
    public void setPhone(String value){
        put(PHONE, value);
    }

    public String getOffice() {
        return (String) get(OFFICE);
    }
    public void setOffice(String value){
        put(OFFICE, value);
    }


}