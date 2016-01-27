package it.polito.mad.polilife.placement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Job;

/**
 * Created by luigi onSelectAppliedJobs 19/01/16.
 */
public class PositionsListFragment extends ListFragment
        implements DBCallbacks.GetListCallback<Job> {

    private static final String TYPE_KEY = "type";

    private String mType;

    public static PositionsListFragment newInstance(String type){
        PositionsListFragment fragment = new PositionsListFragment();
        Bundle args = new Bundle();
        args.putString(TYPE_KEY, type);
        fragment.setArguments(args);
        return fragment;
    }

    //private List<Job> mAppliedJobs;
    private PositionsBaseAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty_result, (ViewGroup) view);
        //getListView().setEmptyView(emptyView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new PositionsBaseAdapter(getActivity());
        mAdapter.setOnClickListener(new PositionsBaseAdapter.OnJobClickListener() {
            @Override
            public void onPositionItemClick(View itemView, int position, Job item) {
                Intent intent = new Intent(getActivity(), PositionDetailsActivity.class);
                intent.putExtra("id", item.getObjectId());
                startActivity(intent);
            }
        });

        setListAdapter(mAdapter);
        String type = getArguments().getString(TYPE_KEY);
        if (type.equalsIgnoreCase("applied")) {
            PoliLifeDB.getAppliedJobs(this);
        }
        else {
            PoliLifeDB.getCachedJobs(this);
        }
    }

    @Override
    public void onFetchSuccess(List<Job> result) {
        mAdapter.setData(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFetchError(Exception exception) {

    }
}
