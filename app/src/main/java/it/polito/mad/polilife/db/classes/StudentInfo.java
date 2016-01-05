package it.polito.mad.polilife.db.classes;

/**
 * Created by Luigi on 27/10/2015.
 */
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import java.util.Date;
import java.util.List;

@ParseClassName("StudentInfo")
public class StudentInfo extends ParseObject {

    public StudentInfo() {
        // A default constructor is required.
    }

    private static final String LANGUAGES = "languages";
    private static final String AVAILABILITYSTART = "availableFrom";
    private static final String AVAILABILITYEND = "availableUntil";
    private static final String FAVOURITE_COMPANIES = "favouriteCompanies";
    private static final String FAVOURITE_OFFERS = "favouriteOffers";
    private static final String CV = "CV";
    private static final String APPLIED = "applied";
    private static final String SKILLS = "skills";

    public Date getAvailableEnd(){
        return (Date) get(AVAILABILITYEND);
    }

    public void setAvailableEnd(Date name){
        put(AVAILABILITYEND, name);
    }

    public Date getAvailableStart(){
        return (Date) get(AVAILABILITYSTART);
    }

    public void setAvailableStart(Date name){
        put(AVAILABILITYSTART, name);
    }

    public List<String> getSkills(){ return (List<String>) get(SKILLS);  }
    public void addSkill(String skill){ getSkills().add(skill); }
    public void setSkills(List<String> value) {   put(SKILLS, value);  }

    public void setCVFile(ParseFile CVFile) {
        put(CV, CVFile);
    }
    public ParseFile getCVFile(ParseFile CVFile) {
        return (ParseFile) get(CV);
    }

    public ParseRelation<Company> getFavoriteCompanies(){
        return getRelation(FAVOURITE_COMPANIES);
    }

    public void addFavoriteCompany(ParseRelation relation, Company company){
        relation.add(company);
    }

    public ParseRelation<Position> getAppliedPositions(){
        return getRelation(APPLIED);
    }
    public void addAppliedPositions(ParseRelation relation, Position position){
        relation.add(position);
    }
    public void removeAppliedPosition(ParseRelation relation, Position position){
        relation.remove(position);
    }

    public ParseRelation<Position> getFavoritePositions(){
        return getRelation(FAVOURITE_OFFERS);
    }
    public void addFavoritePositions(ParseRelation relation, Position position){
        relation.add(position);
    }

    public List<String> getLanguages(){ return (List<String>) get(LANGUAGES);  }
    public void setLanguages(List<String> value) {   put(LANGUAGES, value);  }
}
