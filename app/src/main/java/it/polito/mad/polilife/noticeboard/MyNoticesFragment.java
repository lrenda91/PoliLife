package it.polito.mad.polilife.noticeboard;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Notice;
import it.polito.mad.polilife.noticeboard.add.AddNoticeActivity;

public class MyNoticesFragment extends Fragment
        implements NoticesListener, DBCallbacks.DeleteCallback<Notice> {

    public static MyNoticesFragment newInstance(String noticesType) {
        MyNoticesFragment fragment = new MyNoticesFragment();
        Bundle args = new Bundle();
        args.putString(NoticeBoardActivity.TYPE_EXTRA_KEY, noticesType);
        fragment.setArguments(args);
        return fragment;
    }

    interface NoticeDeleteListener {
        void onNoticeDeleted(Notice notice);
    }

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
        mAdapter.setOnNoticeLongClickListener(new NoticesBaseAdapter.onNoticeLongClickListener() {
            @Override
            public void onClick(View itemView, final int position) {
                String[] items = {
                        getString(R.string.details),
                        getString(R.string.delete),
                        getString(R.string.cancel)
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.add_photo);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        final Notice n = mAdapter.getItem(position);
                        switch (item) {
                            case 0:
                                Intent i = new Intent(getActivity(), NoticeDetailsActivity.class);
                                i.putExtra("data", n.getObjectId());
                                startActivity(i);
                                break;
                            case 1:
                                PoliLifeDB.deleteNotice(n, MyNoticesFragment.this);
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onDeleteSuccess(Notice obj) {
        if (getActivity() instanceof NoticeDeleteListener){
            ((NoticeDeleteListener) getActivity()).onNoticeDeleted(obj);
        }
    }

    @Override
    public void onDeleteError(Exception exception) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                Intent i = new Intent(getActivity(), AddNoticeActivity.class);
                i.putExtra(NoticeBoardActivity.TYPE_EXTRA_KEY, getArguments().getString(NoticeBoardActivity.TYPE_EXTRA_KEY));
                getActivity().startActivityForResult(i,
                        NoticeBoardActivity.PUBLISH_NEW_NOTICE_REQUEST_CODE);
            }
        });
    }

    @Override
    public void update(List<Notice> notices) {
        ParseUser me = ParseUser.getCurrentUser();
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
