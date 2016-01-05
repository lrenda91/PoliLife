package it.polito.mad.polilife.placement;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;

import it.polito.mad.polilife.MainActivity;
import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Position;

public class JobPlacementFragment extends Fragment
        implements DBCallbacks.MultipleFetchCallback<Position> {

    //private ViewPager mViewPager;
    private ListView mOffersListView, mCandidaturesListView;
    private PositionsBaseAdapter mOffersAdapter;

    public JobPlacementFragment() {
    }

    public static JobPlacementFragment newInstance() {
        JobPlacementFragment fragment = new JobPlacementFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_placement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOffersAdapter = new PositionsBaseAdapter(getActivity());
        mOffersAdapter.setOnClickListener(new PositionsBaseAdapter.OnPositionClickListener() {
            @Override
            public void onPositionItemClick(View itemView, int position, Position item) {
                Intent intent = new Intent(getActivity(), PositionDetailsActivity.class);
                String id = item.getObjectId();
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        mOffersListView = (ListView) view.findViewById(R.id.offers_list);
        //mOffersListView = new ListView(getActivity());
        //mCandidaturesListView = new ListView(getActivity());
        //mViewPager = (ViewPager) view.findViewById(R.id.pager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOffersListView.setAdapter(mOffersAdapter);


        final View[] pages = {
                mOffersListView,
                mCandidaturesListView
        };
        final String[] titles = {
                "Offers",
                "Candidatures"
        };

        /*mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pages.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return object instanceof View;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = pages[position];
                container.addView(v);
                return v;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        */
        boolean fromLocalDataStore = !Utility.networkIsUp(getActivity());
        PoliLifeDB.advancedPositionsFilter(null, fromLocalDataStore, this);
    }

    @Override
    public void onFetchSuccess(List<Position> result) {
        mOffersAdapter.setData(result);
        mOffersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFetchError(Exception exception) {
        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
    }

}
