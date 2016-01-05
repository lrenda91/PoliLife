package it.polito.mad.polilife.db.classes;

import com.parse.*;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@ParseClassName("Position")
public class Position extends ParseObject {



    /* Enum values */
    public enum TypeOfJob {
        FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, TEMPORARY, OTHER;
    }
    public enum TypeOfDegree {
        BACHELOR, MASTER
    }



    public static final String NAME = "name";
    public static final String ABOUT = "about";
    public static final String COMPANY = "company";
    public static final String APPLIEDSTUS = "applied";
    public static final String TYPE_OF_JOB = "typeOfJob";
    public static final String TYPE_OF_DEGREE = "typeOfDegree";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String CLOGO = "clogo";
    public static final String START_DATE = "availableFrom";

    public static class Filter implements Serializable {
        public Filter(){
        }
        public String name, typeOfJob, typeOfDegree, city;
        public Date startDate;
        public int daysAgo = -1;
        public Filter name(String value){ name = value; return this; }
        public Filter typeOfJob(String value){ typeOfJob = value; return this; }
        public Filter startDate(Date value){ startDate = value; return this; }
        public Filter typeOfDegree(String value){ typeOfDegree = value; return this; }
        public Filter city(String value){ city = value; return this; }
    }

    public Company getParentCompany(){
        return ((Company) get(COMPANY));
    }
    public void setParentCompany(Company value){
        put(COMPANY, value);
    }

    public List<String> getSkills(){ return (List<String>) get("skills");  }
    public void setSkills(List<String> value) {   put("skills", value);  }

    public String getCity(){
        return (String) get(CITY);
    }
    public void setCity(String location){
        put(CITY, location);
    }

    public String getCountry(){
        return (String) get(COUNTRY);
    }
    public void setCountry(String location){
        put(COUNTRY, location);
    }

    public String getAbout(){
        return (String) get(ABOUT);
    }
    public void setAbout(String location){
        put(ABOUT, location);
    }

    public ParseFile getCLogo(){
        return (ParseFile) get(CLOGO);
    }
    public void setCLogo(ParseFile resumeFile) {
        put(CLOGO, resumeFile);
    }

    public Date getStartDate(){ return (Date) get(START_DATE); }
    public void setStartDate(Date value) { put(START_DATE, value); }

    public String getName(){
        return (String) get(NAME);
    }

    public void setName(String name){
        put(NAME, name);
    }
    public String getTypeOfDegree(){
        return (String) get(TYPE_OF_DEGREE);
    }

    public void setTypeOfDegree(String typeOfDegree){
        put(TYPE_OF_DEGREE, typeOfDegree);
    }
    public String getTypeOfJob(){
        return (String) get(TYPE_OF_JOB);
    }

    public void setTypeOfJob(String typeOfJob){
        put(TYPE_OF_JOB, typeOfJob);
    }


    public ParseRelation<StudentInfo> getRelationAppliedStudents(){
        return getRelation(APPLIEDSTUS);
    }
    public void addAppliedStudents(ParseRelation relation, StudentInfo student){
        relation.add(student);
    }

    public void removeAppliedStudents(ParseRelation relation, StudentInfo student){
        relation.remove(student);
    }


}