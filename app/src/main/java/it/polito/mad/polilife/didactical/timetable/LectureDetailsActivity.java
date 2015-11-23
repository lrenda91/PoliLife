package it.polito.mad.polilife.didactical.timetable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Classroom;
import it.polito.mad.polilife.didactical.ClassroomSelectionListener;
import it.polito.mad.polilife.didactical.ProfessorSelectionListener;
import it.polito.mad.polilife.didactical.rooms.ClassroomDetailsFragment;
import it.polito.mad.polilife.didactical.timetable.data.Lecture;

public class LectureDetailsActivity extends AppCompatActivity
        implements ClassroomSelectionListener, ProfessorSelectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);
        Bundle extras = getIntent().getExtras();

        TextView courseTV = (TextView) findViewById(R.id.courseTextView);
        courseTV.setText(extras.getString("course"));

        final TextView roomTV = (TextView) findViewById(R.id.roomTextView);
        roomTV.setText(extras.getString("room"));

        final TextView teacherTV = (TextView) findViewById(R.id.teacherTextView);
        teacherTV.setText(extras.getString("teacher"));

        TextView scheduleTV = (TextView) findViewById(R.id.scheduleTextView);
        scheduleTV.setText(extras.getString("time"));

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.backgroundCourseLayout);
        rl.setBackgroundColor(getResources().getColor(extras.getInt("color")));

        teacherTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = teacherTV.getText().toString();
                onProfessorSelected(name);
            }
        });

        final View vv = findViewById(R.id.room_info_container);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String room = roomTV.getText().toString();

                PoliLifeDB.searchClassrooms(room, new DBCallbacks.ClassroomSearchCallback() {
                    @Override
                    public void onClassroomsFound(List<Classroom> result) {
                        Classroom classroom = result.get(0);
                        onClassroomSelected(classroom);
                    }

                    @Override
                    public void onClassroomSearchError(Exception exception) {

                    }
                });


            }
        });
    }

    private void showFragment(Fragment fragment){
        FragmentManager frgManager = getSupportFragmentManager();
        frgManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.room_info_container, fragment).commit();
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
