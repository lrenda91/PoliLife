package it.polito.mad.polilife.db.classes;

import com.parse.*;
import java.util.List;

@ParseClassName("Position")
public class Position extends ParseObject {



    /* Enum values */
    public enum TypeOfContract {
        FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, TEMPORARY, OTHER;
    }
    public enum TypeOfJob {

    }



    private static final String NAME = "name";
    private static final String ABOUT = "about";
    private static final String COMPANY = "company";
    private static final String APPLIEDSTUS = "applied";
    private static final String TYPEOFJOB = "typeOfJob";
    private static final String TYPEOFDEGREE = "typeOfDegree";
    private static final String TYPEOFCONTRACT = "typeOfContract";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String CLOGO = "clogo";

    public String getTypeOfContract(){
        return ((String) get(TYPEOFCONTRACT));
    }
    public void setTypeOfContract(String typeOfContract){
        put(TYPEOFCONTRACT, typeOfContract);
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



    public String getName(){
        return (String) get(NAME);
    }

    public void setName(String name){
        put(NAME, name);
    }
    public String getTypeOfDegree(){
        return (String) get(TYPEOFDEGREE);
    }

    public void setTypeOfDegree(String typeOfDegree){
        put(TYPEOFDEGREE, typeOfDegree);
    }
    public String getTypeOfJob(){
        return (String) get(TYPEOFJOB);
    }

    public void setTypeOfJob(String typeOfJob){
        put(TYPEOFJOB, typeOfJob);
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