package it.polito.mad.polilife.profile;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Student;
import it.polito.mad.polilife.db.classes.StudentInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Student mUser;
    private StudentInfo mInfo;

    private ViewGroup contactsLayout, infoLayout;
    private GridView skillsGrid;

    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }

    public ProfileFragment() {
        ParseUser user = ParseUser.getCurrentUser();
        assert user != null &&
                user instanceof Student &&
                ((Student) user).getStudentInfo() != null;
        mUser = (Student) user;
        mInfo = mUser.getStudentInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactsLayout = (ViewGroup) view.findViewById(R.id.contactsStudentLayout);
        infoLayout = (ViewGroup) view.findViewById(R.id.infoStudentLayout);
        skillsGrid = (GridView) view.findViewById(R.id.skills);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Resources res = getActivity().getResources();
        DateFormat df = new SimpleDateFormat(res.getString(R.string.date_format));

        //adding contacts info
        String phone = mUser.getContactPhone() != null ?
                mUser.getContactPhone() : res.getString(R.string.no_phone);
        String mail = mUser.getEmail() != null ?
                mUser.getEmail() : res.getString(R.string.no_mail);
        View phoneView = inflater.inflate(R.layout.layout_list_item_edtable, null, false);
        ((TextView) phoneView.findViewById(R.id.item_text)).setText(phone);
        ((EditText) phoneView.findViewById(R.id.item_editText)).setText(phone);
        View mailView = inflater.inflate(R.layout.layout_list_item_edtable, null, false);
        ((TextView) mailView.findViewById(R.id.item_text)).setText(mail);
        ((EditText) mailView.findViewById(R.id.item_editText)).setText(mail);
        contactsLayout.addView(phoneView);
        contactsLayout.addView(mailView);

        //adding basic info
        String name = mUser.getFirstName() != null ?
                mUser.getFirstName() : res.getString(R.string.no_name);
        String surname = mUser.getLastName() != null ?
                mUser.getLastName() : "" ;
        String complete = name + " " + surname;
        View completeNameView = inflater.inflate(R.layout.layout_list_item_edtable, null, false);
        ((TextView) completeNameView.findViewById(R.id.item_text)).setText(complete);
        ((EditText) completeNameView.findViewById(R.id.item_editText)).setText(complete);
        String dob = mUser.getBirthDate() != null ?
                df.format(mUser.getBirthDate()) : res.getString(R.string.no_dob);
        View dobView = inflater.inflate(R.layout.layout_list_item_edtable, null, false);
        ((TextView) dobView.findViewById(R.id.item_text)).setText(dob);
        ((EditText) dobView.findViewById(R.id.item_editText)).setText(dob);
        String location =
                mUser.getAddress() != null ? mUser.getAddress() : res.getString(R.string.no_address)
                + ", " +
                mUser.getCity() != null ? mUser.getCity() : res.getString(R.string.no_city)
                + ", " +
                mUser.getCountry() != null ? mUser.getCountry() : res.getString(R.string.no_country);
        View locationView = inflater.inflate(R.layout.layout_list_item_edtable, null, false);
        ((TextView) locationView.findViewById(R.id.item_text)).setText(location);
        ((EditText) locationView.findViewById(R.id.item_editText)).setText(location);
        String aboutMe = mUser.getAbout() != null ? mUser.getAbout() : "";
        View aboutView = inflater.inflate(R.layout.layout_list_item_edtable, null, false);
        ((TextView) aboutView.findViewById(R.id.item_text)).setText(aboutMe);
        ((EditText) aboutView.findViewById(R.id.item_editText)).setText(aboutMe);
        infoLayout.addView(completeNameView);
        infoLayout.addView(dobView);
        infoLayout.addView(locationView);
        infoLayout.addView(aboutView);

        List<String> skills = mUser.getStudentInfo().getSkills() != null ?
                mUser.getStudentInfo().getSkills() : new ArrayList<String>();
        skillsGrid.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills));
    }

}
