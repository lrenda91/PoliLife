package it.polito.mad.polilife.noticeboard;


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

public class NoticeboardHomeFragment extends Fragment {

    public static NoticeboardHomeFragment newInstance() {
        NoticeboardHomeFragment fragment = new NoticeboardHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NoticeboardHomeFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_noticeboard_home, container, false);

        final Intent intent = new Intent(getActivity(), NoticeBoardActivity.class);
        root.findViewById(R.id.home_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().startActivity(intent);
            }
        });

        root.findViewById(R.id.books_notes_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().startActivity(intent);
            }
        });

        return root;
    }


}
