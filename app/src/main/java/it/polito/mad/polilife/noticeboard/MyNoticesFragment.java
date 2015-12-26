package it.polito.mad.polilife.noticeboard;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Notice;
import it.polito.mad.polilife.noticeboard.add.AddNoticeActivity;

public class MyNoticesFragment extends Fragment implements NoticesListener {

    private ListView mListView;
    private NoticesBaseAdapter mAdapter;

    public MyNoticesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NoticesBaseAdapter(getActivity());
        mAdapter.setOnNoticeClickListener(new NoticesBaseAdapter.onNoticeClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                Notice n = mAdapter.getItem(position);
                Intent i = new Intent(getActivity(), NoticeDetailsActivity.class);
                i.putExtra("data", n.getObjectId());
                startActivity(i);
            }
        });
    }

    public static MyNoticesFragment newInstance(String param1, String param2) {
        MyNoticesFragment fragment = new MyNoticesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_notices, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.my_notices_list);
        mListView.setAdapter(mAdapter);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), AddNoticeActivity.class));
            }
        });
    }

    @Override
    public void update(List<Notice> notices) {
        ParseUser me = ParseUser.getCurrentUser();
        assert me != null;
        List<Notice> mine = new LinkedList<>();
        for (Notice n : notices){
            if (n.getOwner() != null && n.getOwner().getObjectId() != null &&
                    n.getOwner().getObjectId().equals(me.getObjectId())){
                mine.add(n);
            }
        }
        mAdapter.setData(mine);
        mAdapter.notifyDataSetChanged();
    }

}
