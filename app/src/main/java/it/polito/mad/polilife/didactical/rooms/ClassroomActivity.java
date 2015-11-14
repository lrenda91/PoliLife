package it.polito.mad.polilife.didactical.rooms;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Classroom;
import it.polito.mad.polilife.didactical.DidacticalHomeFragment;

public class ClassroomActivity extends AppCompatActivity
        implements ClassroomSearchFragment.ClassroomSelectionListener {

    private void showFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.classroom_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_search);

        final EditText et = (EditText) findViewById(R.id.search_edit_text);
        findViewById(R.id.search_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = et.getText().toString();
                Fragment results = ClassroomSearchFragment.newInstance(param);
                showFragment(results);
            }
        });
    }

    @Override
    public void onClassroomSelected(Classroom classroom) {
        Fragment fragment = ClassroomDetailsFragment.newInstance(
                classroom.getName(),
                classroom.getLocation().getLatitude(),
                classroom.getLocation().getLongitude(),
                classroom.getDetails()
        );
        showFragment(fragment);
    }

}
