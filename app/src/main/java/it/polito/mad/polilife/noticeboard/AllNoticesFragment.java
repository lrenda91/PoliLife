package it.polito.mad.polilife.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Notice;
import it.polito.mad.polilife.noticeboard.add.AddNoticeActivity;


public class AllNoticesFragment extends Fragment
    implements NoticesListener {

    public static AllNoticesFragment newInstance(){
        return new AllNoticesFragment();
    }

    private ExpandableListView mExpListView;
    private HashMap<String, List<Notice>> map = new HashMap<>();

    public AllNoticesFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map.put("1", new LinkedList<Notice>());
        map.put("2", new LinkedList<Notice>());
    }

    @Override
    public void update(List<Notice> notices) {
        map.put("1", notices);
        mExpListView.setAdapter(
                new ExpandableListAdapter(getActivity(), new LinkedList<>(map.keySet()), map));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notices_all, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mExpListView = (ExpandableListView) view.findViewById(R.id.lvExp);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<Notice>> _listDataChild;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<Notice>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Notice getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(_context).inflate(R.layout.layout_notice_item, parent, false);
            }
            Notice n = getChild(groupPosition, childPosition);
            ((TextView)convertView.findViewById(R.id.offer_title)).setText(n.getTitle());
            ((TextView)convertView.findViewById(R.id.offer_price)).setText(n.getPrice()+"");
            ((TextView)convertView.findViewById(R.id.offer_location)).setText(n.getLocationName());
            ((TextView)convertView.findViewById(R.id.offer_date)).setText(n.getAvailableFrom().toString());
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
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
            String headerTitle = (String) getGroup(groupPosition);
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
