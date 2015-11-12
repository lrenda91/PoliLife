package it.polito.mad.polilife.db.classes;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("_User")
public class Student extends ParseUser {

    private static final String STUDENT_KEY = "studentInfo";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String DOB = "birth";
    private static final String ADDRESS= "address";
    private static final String COUNTRY = "country";
    private static final String CITY= "city";
    private static final String CONTACT_PHONE = "contactPhone";
    private static final String ABOUT = "aboutMe";
    private static final String PHOTO= "photo";

    public Student() {

    }

    public StudentInfo getStudentInfo(){ return (StudentInfo) get(STUDENT_KEY); }
    public void setStudentInfo(StudentInfo value){ put(STUDENT_KEY, value); }

    public String getFirstName(){
        return (String) get(FIRST_NAME);
    }
    public void setFirstName(String value){
        put(FIRST_NAME, value);
    }

    public String getLastName(){
        return (String) get(LAST_NAME);
    }
    public void setLastName(String value){
        put(LAST_NAME, value);
    }

    public String getContactPhone(){
        return (String) get(CONTACT_PHONE);
    }
    public void setContactPhone(String contactName){
        put(CONTACT_PHONE, contactName);
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

    public Date getBirthDate(){ return (Date) get(DOB); }
    public void setBirthDate(Date value){ put(DOB, value); }

    public ParseFile getPhoto(){ return (ParseFile) get(PHOTO); }
    public void setPhoto(ParseFile value){ put(PHOTO, value); }

    public String getAbout(){ return (String) get(ABOUT); }
    public void setAbout(String value){ put(ABOUT, value); }

}