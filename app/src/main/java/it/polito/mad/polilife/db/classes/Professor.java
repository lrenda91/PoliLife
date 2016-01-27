package it.polito.mad.polilife.db.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by luigi onSelectAppliedJobs 29/05/15.
 */
@ParseClassName("Professor")
public class Professor extends ParseObject implements Parcelable {

    static final String NUMBER = "number";
    static final String NAME = "name";
    static final String EMAIL = "email";
    static final String PHONE = "phone";
    static final String OFFICE = "office";
    static final String OFFICE_HOURS = "officeHours";
    public static final String PHOTO = "photo";

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Professor createFromParcel(Parcel in) {
            return new Professor(in);
        }
        public Professor[] newArray(int size) {
            return new Professor[size];
        }
    };

    public Professor(Parcel in){
        String number = in.readString();
        String name = in.readString();
        String mail = in.readString();
        String phone = in.readString();
        String office = in.readString();
        String hours = in.readString();
        if (number != null) put(NUMBER, number);
        if (name != null) put(NAME, name);
        if (mail != null) put(EMAIL, mail);
        if (phone != null) put(PHONE, phone);
        if (office != null) put(OFFICE, office);
        if (hours != null) put(OFFICE_HOURS, hours);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getString(NUMBER));
        dest.writeString(getString(NAME));
        dest.writeString(getString(EMAIL));
        dest.writeString(getString(PHONE));
        dest.writeString(getString(OFFICE));
        dest.writeString(getString(OFFICE_HOURS));
    }

    public Professor() {

    }

    public String getName() {
        return (String) get(NAME);
    }
    public void setName(String value){
        put(NAME, value);
    }

    public String getIDNumber() {
        return getString(NUMBER);
    }
    public void setIDNumber(String value){
        put(NUMBER, value);
    }

    public String getEmail() {
        return getString(EMAIL);
    }
    public void setEmail(String value){
        put(EMAIL, value);
    }

    public String getPhone() {
        return getString(PHONE);
    }
    public void setPhone(String value){
        put(PHONE, value);
    }

    public String getOffice() {
        return getString(OFFICE);
    }
    public void setOffice(String value){
        put(OFFICE, value);
    }

    public String getOfficeHours(){ return getString(OFFICE_HOURS); }
    public void setOfficeHours(String value){ put(OFFICE_HOURS, value); }

    public ParseFile getPhoto(){ return (ParseFile) get(PHOTO); }
    public void setPhoto(ParseFile value){ put(PHOTO, value); }

}