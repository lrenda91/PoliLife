package it.polito.mad.polilife.didactical.rooms;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import it.polito.mad.polilife.R;

public class ClassroomActivity extends AppCompatActivity implements Int {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_search);

        Fragment fragment = ClassroomSearchFragment.newInstance();


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.classroom_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void fai() {
        Fragment fragment = ClassroomDetailsFragment.newInstance("5I",45,7,null);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.classroom_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
