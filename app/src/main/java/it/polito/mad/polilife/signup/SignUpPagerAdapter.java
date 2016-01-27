package it.polito.mad.polilife.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import it.polito.mad.polilife.MainActivity;
import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Student;
import it.polito.mad.polilife.db.classes.StudentInfo;
import it.polito.mad.polilife.db.parcel.PStudentData;
import it.polito.mad.polilife.db.parcel.PUserData;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SignUpPagerAdapter extends FragmentPagerAdapter {

    private PUserData userData = new PUserData();

    private Fragment[] pages;

    public SignUpPagerAdapter(FragmentManager fm) {
        super(fm);
        pages = new Fragment[]{
                Page1Fragment.newInstance(userData),
                Page2Fragment.newInstance(userData)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return pages[position];
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
        }
        return null;
    }

    public static class Page1Fragment extends Fragment implements View.OnClickListener {
        public static Page1Fragment newInstance(PUserData userData) {
            Page1Fragment fragment = new Page1Fragment();
            Bundle args = new Bundle();
            args.putParcelable("userData", userData);
            fragment.setArguments(args);
            return fragment;
        }

        public Page1Fragment() {
        }

        EditText firstNameEditText;
        EditText lastNameEditText;
        EditText cityEditText;
        Spinner country;

        PUserData info;
        ProgressDialog waitingDialog;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_sign_up_page1, container, false);
            waitingDialog = new ProgressDialog(getActivity());
            waitingDialog.setMessage("wait");
            waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            waitingDialog.setCancelable(false);
            waitingDialog.setIndeterminate(true);
            firstNameEditText = (EditText) root.findViewById(R.id.firstname_editText);
            lastNameEditText = (EditText) root.findViewById(R.id.lastname_editText);
            cityEditText = (EditText) root.findViewById(R.id.student_city_editText);
            country = (Spinner) root.findViewById(R.id.spinner_country);

            info = getArguments().getParcelable("userData");

            Button signUpButton = (Button) root.findViewById(R.id.register_student_signup_button);
            signUpButton.setOnClickListener(this);

            return root;
        }

        @Override
        public void onClick(View v) {
            /*if (!Utility.networkIsUp(getActivity())){
                Toast.makeText(getActivity(),
                        "No network",
                        Toast.LENGTH_LONG).show();
                return;
            }
            */
        }
    }

    public static class Page2Fragment extends Fragment
            implements DBCallbacks.StudentSignUpCallback, View.OnClickListener {
        public static Page2Fragment newInstance(PUserData userData) {
            Page2Fragment fragment = new Page2Fragment();
            Bundle args = new Bundle();
            args.putParcelable("userData", userData);
            fragment.setArguments(args);
            return fragment;
        }

        public Page2Fragment() {
        }

        EditText usernameEditText;
        EditText emailEditText;
        EditText passwordEditText;

        PUserData info;
        ProgressDialog waitingDialog;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_sign_up_page2, container, false);
            waitingDialog = new ProgressDialog(getActivity());
            waitingDialog.setMessage("wait");
            waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            waitingDialog.setCancelable(false);
            waitingDialog.setIndeterminate(true);
            usernameEditText = (EditText) root.findViewById(R.id.username_editText);
            emailEditText = (EditText) root.findViewById(R.id.studentregister_email_editText);
            passwordEditText = (EditText) root.findViewById(R.id.studentregister_password_editText);

            info = getArguments().getParcelable("userData");

            Button signUpButton = (Button) root.findViewById(R.id.register_student_signup_button);
            signUpButton.setOnClickListener(this);

            return root;
        }

        @Override
        public void onClick(View v) {
            if (!Utility.networkIsUp(getActivity())){
                Toast.makeText(getActivity(),
                        "No network",
                        Toast.LENGTH_LONG).show();
                return;
            }

            EditText[] editTexts = {
                    usernameEditText, emailEditText, passwordEditText
            };
            for (EditText et : editTexts){
                if (et.getText() == null || et.getText().toString().isEmpty()){
                    //track error onSelectAppliedJobs editText
                    Toast.makeText(getActivity(),
                            "Please complete the sign up form",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            /* Now inputs are valid */

            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();


            /* Now, try to sign up */
            info.username(username)
                    .password(password)
                    .eMail(email);

            PoliLifeDB.signUpStudent(info, new PStudentData(), this);


            waitingDialog.show();
        }

        @Override
        public void onStudentSignUpException(Exception pe) {
            waitingDialog.dismiss();
            Toast.makeText(getActivity(),
                    pe.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStudentSignUpSuccess(Student student) {
            waitingDialog.dismiss();


            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }


}