package it.polito.mad.polilife.db.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import it.polito.mad.polilife.db.classes.Student;


/**
 * Parcelable wrapper for all student data, so that they can be forwarded through intents
 */
public class PUserData implements DBObjectBuilder<Student>, Parcelable {

    private String username, password, firstName, lastName, eMail,
            city, country, address, phone, about;
    private Date birthDate;
    private PFileData photo;

    public PUserData(){

    }

    public static final Creator CREATOR = new Creator() {
        public PUserData createFromParcel(Parcel in) {
            return new PUserData(in);
        }
        public PUserData[] newArray(int size) {
            return new PUserData[size];
        }
    };

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getFirstName(){ return firstName; }
    public String getLastName(){ return lastName; }
    public String getEmail(){ return eMail; }
    public String getCity() {
        return city;
    }
    public String getCountry() {
        return country;
    }
    public String getAddress() {
        return address;
    }
    public String getPhone() {
        return phone;
    }
    public String getAbout() {
        return about;
    }
    public Date getBirthDate() {
        return birthDate;
    }
    public PFileData getPhoto() {
        return photo;
    }

    public PUserData username(String value){ username = value; return this; }
    public PUserData password(String value){ password = value; return this; }
    public PUserData firstName(String value){ firstName = value; return this; }
    public PUserData lastName(String value){ lastName = value; return this; }
    public PUserData eMail(String value){ eMail = value; return this; }
    public PUserData city(String value){ city = value; return this; }
    public PUserData country(String value){ country = value; return this; }
    public PUserData address(String value){ address = value; return this; }
    public PUserData phone(String value){ phone = value; return this; }
    public PUserData about(String value){ about = value; return this; }

    public PUserData(Parcel in){
        username = in.readString();
        password = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        eMail = in.readString();
        city = in.readString();
        country = in.readString();
        address = in.readString();
        phone = in.readString();
        about = in.readString();
        birthDate = new Date(in.readLong());
        photo = in.readParcelable(PFileData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(eMail);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(about);
        dest.writeLong(birthDate.getTime());
        dest.writeParcelable(photo, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Student build() {
        Student user = new Student();
        if (username != null) user.setUsername(username);
        if (password != null) user.setPassword(password);
        if (eMail != null) user.setEmail(eMail);
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (country != null) user.setCountry(country);
        if (address != null) user.setAddress(address);
        if (phone != null) user.setContactPhone(phone);
        if (about != null) user.setAbout(about);
        if (birthDate != null) user.setBirthDate(birthDate);
        if (photo != null) user.setPhoto(photo.build());
        return user;
    }

    @Override
    public void fillFrom(Student obj) {

    }
}
