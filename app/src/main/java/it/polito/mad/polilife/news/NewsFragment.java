package it.polito.mad.polilife.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Notice;
import it.polito.mad.polilife.db.classes.Position;
import it.polito.mad.polilife.didactical.prof.ProfessorsActivity;
import it.polito.mad.polilife.didactical.rooms.ClassroomActivity;
import it.polito.mad.polilife.didactical.timetable.TimetableActivity;
import it.polito.mad.polilife.noticeboard.NoticeDetailsActivity;

public class NewsFragment extends Fragment
        implements DBCallbacks.MultipleFetchCallback<ParseObject> {

    private HashMap<String, List<ParseObject>> map;
    private ExpandableListAdapter mAdapter;
    private static final String NOTICES_KEY = "Recent notices";
    private static final String POSITIONS_KEY = "Recent positions";

    private ProgressBar mWait;

    public NewsFragment() {
        map = new HashMap<>();
        map.put(NOTICES_KEY, new LinkedList<ParseObject>());
        map.put(POSITIONS_KEY, new LinkedList<ParseObject>());
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mWait = (ProgressBar) view.findViewById(R.id.wait);
        mWait.setVisibility(View.VISIBLE);
        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        mAdapter = new ExpandableListAdapter(getActivity(), map);
        expandableListView.setAdapter(mAdapter);

        final Activity myActivity = getActivity();
        view.findViewById(R.id.timetable_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(myActivity, TimetableActivity.class);
                //i.putExtra("model", data);
                myActivity.startActivity(i);
            }
        });

        view.findViewById(R.id.classrooms_select).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myActivity.startActivity(new Intent(myActivity, ClassroomActivity.class));
            }
        });

        view.findViewById(R.id.teachers_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myActivity.startActivity(new Intent(myActivity, ProfessorsActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean fromLocalDataStore = !Utility.networkIsUp(getActivity());
        PoliLifeDB.getRecentNoticesAndPositions(100, fromLocalDataStore, this);
    }

    @Override
    public void onFetchSuccess(List<ParseObject> result) {
        map.get(POSITIONS_KEY).clear();
        map.get(NOTICES_KEY).clear();
        mWait.setVisibility(View.INVISIBLE);
        for (ParseObject obj : result){
            if (obj instanceof Notice) map.get(NOTICES_KEY).add(obj);
            else if (obj instanceof Position) map.get(POSITIONS_KEY).add(obj);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFetchError(Exception exception) {
        mWait.setVisibility(View.INVISIBLE);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader;
        private HashMap<String, List<ParseObject>> _listDataChild;

        public ExpandableListAdapter(Context context, HashMap<String, List<ParseObject>> map) {
            this._context = context;
            this._listDataHeader = new LinkedList<>(map.keySet());
            this._listDataChild = map;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final Object child = getChild(groupPosition, childPosition);
            if (child instanceof Notice){
                if (convertView == null) {
                    convertView = LayoutInflater.from(_context).inflate(
                            R.layout.layout_notice_item, parent, false);
                }
                final Notice n = (Notice) child;
                String title = n.getTitle() != null ? n.getTitle() : _context.getString(R.string.no_title);
                String location = n.getLocationName() != null ? n.getLocationName() :
                        _context.getString(R.string.no_location);
                String from = n.getAvailableFrom() != null ? n.getAvailableFrom().toString() :
                        _context.getString(R.string.no_available_from);
                ((TextView)convertView.findViewById(R.id.offer_title)).setText(title);
                ((TextView)convertView.findViewById(R.id.offer_price)).setText(n.getPrice()+"");
                ((TextView)convertView.findViewById(R.id.offer_location)).setText(location);
                ((TextView)convertView.findViewById(R.id.offer_date)).setText(from);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), NoticeDetailsActivity.class);
                        i.putExtra("data", n.getObjectId());
                        startActivity(i);
                    }
                });
            }
            else if (child instanceof Position){
                if (convertView == null) {
                    convertView = LayoutInflater.from(_context).inflate(
                            R.layout.layout_position_item, parent, false);
                }
                Position p = (Position) child;
                ((TextView) convertView.findViewById(R.id.offer_title)).setText(p.getName());
                ((TextView)convertView.findViewById(R.id.offer_location)).setText(p.getCity());
                ((TextView)convertView.findViewById(R.id.offer_date)).setText(p.getStartDate().toString());
            }
            else{
                throw new RuntimeException();
            }
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = getGroup(groupPosition);
            if (convertView == null) {
                convertView = new TextView(_context);
                ((TextView) convertView).setTypeface(null, Typeface.BOLD);
                ((TextView) convertView).setText(headerTitle);
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
