package it.polito.mad.polilife.didactical.timetable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Classroom;
import it.polito.mad.polilife.didactical.ClassroomSelectionListener;
import it.polito.mad.polilife.didactical.ProfessorSelectionListener;
import it.polito.mad.polilife.didactical.rooms.ClassroomDetailsFragment;

public class LectureDetailsActivity extends AppCompatActivity
        implements ClassroomSelectionListener,
        ProfessorSelectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);
        Bundle extras = getIntent().getExtras();

        showFragment(LectureDetailsFragment.newInstance(
                extras.getString("course"),
                extras.getString("teacher"),
                extras.getString("time"),
                extras.getString("room"),
                extras.getInt("color"))
        );
    }

    private void showFragment(Fragment fragment){
        FragmentManager frgManager = getSupportFragmentManager();
        frgManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.lecture_container, fragment).commit();
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

    @Override
    public void onProfessorSelected(String professorName) {

    }

}
