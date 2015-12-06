package it.polito.mad.polilife.didactical.timetable.data;

import java.io.Serializable;

/**
 * Created by luigi on 03/12/15.
 */
public class Professor implements Serializable {

    private String ID, name, email, phone, office;

    public Professor(String ID, String name, String mail, String phone, String office){
        this.ID = ID;
        this.name = name;
        this.email = mail;
        this.phone = phone;
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getID() {
        return ID;
    }

}
