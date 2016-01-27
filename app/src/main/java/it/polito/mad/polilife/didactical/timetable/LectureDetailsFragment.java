package it.polito.mad.polilife.didactical.timetable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Classroom;
import it.polito.mad.polilife.didactical.ClassroomSelectionListener;
import it.polito.mad.polilife.didactical.ProfessorSelectionListener;

/**
 * Created by luigi onSelectAppliedJobs 16/11/15.
 */
public class LectureDetailsFragment extends Fragment {

    public static LectureDetailsFragment newInstance(String course, String teacher, String time, String room, int color){
        LectureDetailsFragment fragment = new LectureDetailsFragment();
        Bundle args = new Bundle();
        args.putString("course", course);
        args.putString("teacher", teacher);
        args.putString("time", time);
        args.putString("room", room);
        args.putInt("color", color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lecture_details, container, false);

        Bundle args = getArguments();
        TextView courseTV = (TextView) root.findViewById(R.id.courseTextView);
        courseTV.setText(args.getString("course"));

        final TextView roomTV = (TextView) root.findViewById(R.id.roomTextView);
        roomTV.setText(args.getString("room"));

        final TextView teacherTV = (TextView) root.findViewById(R.id.teacherTextView);
        teacherTV.setText(args.getString("teacher"));

        TextView scheduleTV = (TextView) root.findViewById(R.id.scheduleTextView);
        scheduleTV.setText(args.getString("time"));

        RelativeLayout rl = (RelativeLayout) root.findViewById(R.id.backgroundCourseLayout);
        rl.setBackgroundColor(getResources().getColor(args.getInt("color")));

        teacherTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = teacherTV.getText().toString();
                if (getActivity() instanceof ProfessorSelectionListener) {
                    ((ProfessorSelectionListener) getActivity()).onProfessorSelected(name);
                }
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String room = roomTV.getText().toString();

                /*PoliLifeDB.searchClassrooms(room, new DBCallbacks.ClassroomSearchCallback() {
                    @Override
                    public void onClassroomsFound(List<Classroom> result) {
                        Classroom classroom = result.get(0);
                        if (getActivity() instanceof ClassroomSelectionListener) {
                            ((ClassroomSelectionListener) getActivity()).onClassroomSelected(classroom);
                        }
                    }

                    @Override
                    public void onClassroomSearchError(Exception exception) {

                    }
                });
*/

            }
        });

        return root;
    }
}