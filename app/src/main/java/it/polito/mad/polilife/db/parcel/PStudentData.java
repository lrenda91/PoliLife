package it.polito.mad.polilife.db.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

import it.polito.mad.polilife.db.classes.StudentInfo;

/**
 * Parcelable wrapper for a Student instance, so that its data can be forwarded through intents
 * Created by luigi on 10/06/15.
 */
public class PStudentData implements DBObjectBuilder<StudentInfo>, Parcelable {

    private Date availableFrom, availableTo;
    private List<String> languages;
    private PFileData CV;

    public static final Creator CREATOR = new Creator() {
        public PStudentData createFromParcel(Parcel in) {
            return new PStudentData(in);
        }
        public PStudentData[] newArray(int size) {
            return new PStudentData[size];
        }
    };

    public PStudentData(){

    }

    public Date getAvailableFrom() {
        return availableFrom;
    }
    public Date getAvailableTo() {
        return availableTo;
    }
    public List<String> getLanguages() {
        return languages;
    }
    public PFileData getCV() {
        return CV;
    }

    public PStudentData(Parcel in){
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public StudentInfo build() {
        StudentInfo result = new StudentInfo();
        if (availableFrom != null) result.setAvailableStart(availableFrom);
        if (availableTo != null) result.setAvailableEnd(availableTo);
        if (languages != null) result.setLanguages(languages);
        if (CV != null) result.setCVFile(CV.build());
        return result;
    }

    @Override
    public void fillFrom(StudentInfo obj) {

    }
}
