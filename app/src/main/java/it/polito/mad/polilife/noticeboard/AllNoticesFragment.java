package it.polito.mad.polilife.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Notice;


public class AllNoticesFragment extends Fragment implements NoticesListener {

    public static AllNoticesFragment newInstance(){
        return new AllNoticesFragment();
    }

    private ListView mListView;
    private NoticesBaseAdapter mAdapter;

    public AllNoticesFragment(){
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

    @Override
    public void update(List<Notice> notices) {
        mAdapter.setData(notices);
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notices_all, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.all_notices_list);
        mListView.setAdapter(mAdapter);
    }




}
