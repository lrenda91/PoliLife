package it.polito.mad.polilife.didactical;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.didactical.prof.ProfessorsActivity;
import it.polito.mad.polilife.didactical.rooms.ClassroomActivity;
import it.polito.mad.polilife.didactical.timetable.TimetableActivity;
import it.polito.mad.polilife.didactical.timetable.data.Timetable;
import it.polito.mad.polilife.didactical.timetable.data.TimetableImpl;

/**
 * Created by luigi onSelectAppliedJobs 13/11/15.
 */
public class DidacticalHomeFragment extends Fragment {

    public static DidacticalHomeFragment newInstance(){
        return new DidacticalHomeFragment();
    }

    private Timetable data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            InputStream lecturesStream = null, coursesStream = null;
            try {
                lecturesStream = getActivity().getAssets().open("timetable.json");
                coursesStream = getActivity().getAssets().open("courses.json");
            } catch (IOException e) {}
            data = TimetableImpl.newInstance(coursesStream, lecturesStream);
        }
        else{
            data = (Timetable) savedInstanceState.getSerializable("model");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putSerializable("model", data);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            data = (Timetable) savedInstanceState.getSerializable("model");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_didactical, container, false);

        final Activity myActivity = getActivity();
        root.findViewById(R.id.timetable_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(myActivity, TimetableActivity.class);
                i.putExtra("model", data);
                myActivity.startActivity(i);
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
