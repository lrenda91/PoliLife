package it.polito.mad.polilife.didactical.timetable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class LectureDetailsActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle extras = getIntent().getExtras();
        Lecture data = (Lecture) extras.getSerializable("lecture");

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, extras.getInt("color")));


        if (data.getCourse() != null && data.getCourse().getName() != null) {
            ((TextView) findViewById(R.id.courseTextView)).setText(data.getCourse().getName());
        }

        ViewGroup details = (ViewGroup) findViewById(R.id.lecture_details);
        LayoutInflater inflater = LayoutInflater.from(this);
        if (data.getCourse() != null && data.getCourse().getProfessor() != null &&
                data.getCourse().getProfessor().getName() != null){
            View item = inflater.inflate(R.layout.layout_material_list_item, details, false);
            ((ImageView) item.findViewById(R.id.rowIcon)).setImageResource(R.drawable.ic_mail);
            ((TextView) item.findViewById(R.id.rowText)).setText(data.getCourse().getProfessor().getName());
            details.addView(item);
        }
        if (data.getClassroom() != null){
            View item = inflater.inflate(R.layout.layout_material_list_item, details, false);
            ((ImageView) item.findViewById(R.id.rowIcon)).setImageResource(R.drawable.ic_mail);
            ((TextView) item.findViewById(R.id.rowText)).setText(data.getClassroom());
            details.addView(item);
        }
        if (data.getStartTime() != null && data.getEndTime() != null){
            View item = inflater.inflate(R.layout.layout_material_list_item, details, false);
            ((ImageView) item.findViewById(R.id.rowIcon)).setImageResource(R.drawable.ic_mail);
            ((TextView) item.findViewById(R.id.rowText)).setText(
                    data.getStartTime() + " - " + data.getEndTime());
            details.addView(item);
        }

/*
        String room = roomTV.getText().toString();
        PoliLifeDB.searchClassrooms(room, new DBCallbacks.ClassroomSearchCallback() {
            @Override
            public void onClassroomsFound(List<Classroom> result) {
                Classroom classroom = result.get(0);
                Fragment fragment = ClassroomDetailsFragment.newInstance(
                        classroom.getName(),
                        classroom.getLocation().getLatitude(),
                        classroom.getLocation().getLongitude(),
                        classroom.getDetails()
                );
                showFragment(fragment);
            }

            @Override
            public void onClassroomSearchError(Exception exception) {

            }
        });
        */
    }

    private void showFragment(Fragment fragment){
        FragmentManager frgManager = getSupportFragmentManager();
        frgManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.room_info_container, fragment).commit();
    }

}
