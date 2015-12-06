package it.polito.mad.polilife.profile;


import android.app.Activity;
import android.os.Bundle;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Student user = (Student) ParseUser.getCurrentUser();

    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }

    private FrameLayout headerStudent;
    //private OnScrollListener mListener;

    public ProfileFragment() {
    }

    //Student contacts
    private int[] contactsIcons = {R.drawable.ic_mail, R.drawable.ic_call_grey600_24dp};
    private String[] contactsTypes = {"email", "phone"};

    //Student Info
    private String[] itemPersonalInfoStudent = {"DateOfBirth", "Gender", "Nationality", "Country", "City"
            , "Address"};
    private String[] infoTypes = {"dateOfBirth", "gender", "nationality", "country", "city"
            , "address"};
    private ArrayList<String> skills = new ArrayList<>();

    private Map<String, String> contactsMap, infoMap;

    //@Override
    public void updateContent() {
        Student student = user;
        contactsMap = new HashMap<>();
        contactsMap.put("name", student.getFirstName()+ " "+ student.getLastName());
        contactsMap.put("email", ParseUser.getCurrentUser().getUsername());
        contactsMap.put("phone", student.getContactPhone());

        infoMap = new HashMap<>();
        Date birthDate = student.getBirthDate();
        if (birthDate != null){
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            infoMap.put("dateOfBirth", df.format(student.getBirthDate()));
        }
        else {
            infoMap.put("dateOfBirth", "Not available");
        }

        //infoMap.put("gender", student.getGender());
        //infoMap.put("nationality", student.getNationality());
        infoMap.put("country", student.getCountry());
        infoMap.put("city", student.getCity());
        infoMap.put("address", student.getAddress());


        List<String> skillssList = student.getStudentInfo().getSkills();
        if(skillssList!=null){
            skills = new ArrayList<>(skillssList);
        }else{
            skills= new ArrayList<>();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateContent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        headerStudent = (FrameLayout) view.findViewById(R.id.headerStudent);
        //listener for the scrolling effect
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollViewStudent);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollX = scrollView.getScrollX(); //for horizontalScrollView
                int scrollY = scrollView.getScrollY(); //for verticalScrollView
/*
                if (mListener != null) {
                    abbassaHeader(scrollY);
                    mListener.onScrolled(scrollX, scrollY);
                }*/

            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity)
            ((AppCompatActivity) activity).getSupportActionBar().setTitle(contactsMap.get("name"));

        LinearLayout contactsLayout = (LinearLayout) getActivity().findViewById(R.id.contactsStudentLayout);
        View infoItem = null;
        for (int i = 0; i < contactsTypes.length; i++) {
            infoItem = getActivity().getLayoutInflater().inflate(R.layout.layout_list_item_edtable, null);
            contactsLayout.addView(infoItem);

            ImageView icon = (ImageView) infoItem.findViewById(R.id.item_icon);
            icon.setImageDrawable(getActivity().getResources().getDrawable(contactsIcons[i]));
            TextView text = (TextView) infoItem.findViewById(R.id.item_text);
            text.setText(contactsMap.get(contactsTypes[i]));
        }

        LinearLayout personalInfoLayout = (LinearLayout) getActivity().findViewById(R.id.infoStudentLayout);

        for (int i = 0; i < infoTypes.length; i++) {
            infoItem = getActivity().getLayoutInflater().inflate(R.layout.item_personalinfo_student, null);
            personalInfoLayout.addView(infoItem);

            TextView text = (TextView) infoItem.findViewById(R.id.itemPersonaInfoStudent);
            text.setText((itemPersonalInfoStudent[i]));

            TextView textContent = (TextView) infoItem.findViewById(R.id.itemPersonaInfoStudentContent);
            textContent.setText(infoMap.get(infoTypes[i]));
        }

        if(!skills.isEmpty()){
            //Fill fields of work company
            TextView textSkills = (TextView) getActivity().findViewById(R.id.skills);
            for (int k = 0; k < skills.size(); k++) {
                textSkills.append(skills.get(k));
                if(k < skills.size()-1){
                    textSkills.append(" - ");
                }
            }
        }
    }

    private void abbassaHeader(int dy) {
        headerStudent.setTranslationY(dy / 2);
    }

}
