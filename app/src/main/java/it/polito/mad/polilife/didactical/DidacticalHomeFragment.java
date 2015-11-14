package it.polito.mad.polilife.didactical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.didactical.prof.ProfessorsActivity;
import it.polito.mad.polilife.didactical.rooms.ClassroomActivity;
import it.polito.mad.polilife.didactical.timetable.TimetableActivity;

/**
 * Created by luigi on 13/11/15.
 */
public class DidacticalHomeFragment extends Fragment {

    public static DidacticalHomeFragment newInstance(){
        return new DidacticalHomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_didactical_home, container, false);

        final Activity myActivity = getActivity();
        root.findViewById(R.id.timetable_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myActivity.startActivity(new Intent(myActivity, TimetableActivity.class));
            }
        });

        root.findViewById(R.id.classrooms_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myActivity.startActivity(new Intent(myActivity, ClassroomActivity.class));
            }
        });

        root.findViewById(R.id.teachers_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myActivity.startActivity(new Intent(myActivity, ProfessorsActivity.class));
            }
        });

        return root;
    }
}
