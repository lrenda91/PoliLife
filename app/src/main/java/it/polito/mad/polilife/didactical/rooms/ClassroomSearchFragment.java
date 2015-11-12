package it.polito.mad.polilife.didactical.rooms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.classes.Classroom;

/**
 * Created by luigi on 12/11/15.
 */
public class ClassroomSearchFragment extends Fragment {

    public static ClassroomSearchFragment newInstance(){
        Bundle args = new Bundle();
        ClassroomSearchFragment fragment = new ClassroomSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_classroom_search, container, false);

        final List<String> items = new LinkedList<>();
        items.add("7I"); items.add("5I");
        ListView list = (ListView) root.findViewById(R.id.classroom_results);
        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return items.size();
            }

            @Override
            public String getItem(int position) {
                return items.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = new TextView(getActivity());
                final String item = getItem(position);
                tv.setText(item);
                tv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof Int){
                            ((Int) getActivity()).fai();
                        }
                    }
                });
                return tv;
            }
        });


        return root;
    }


}
