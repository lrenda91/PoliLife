package it.polito.mad.polilife.didactical.rooms;

import android.app.Activity;
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
import android.widget.Toast;

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
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Classroom;

/**
 * Created by luigi on 12/11/15.
 */
public class ClassroomSearchFragment extends Fragment implements DBCallbacks.ClassroomSearchCallback {

    public interface ClassroomSelectionListener {
        void onClassroomSelected(Classroom classroom);
    }

    public static ClassroomSearchFragment newInstance(String searchParam){
        Bundle args = new Bundle();
        ClassroomSearchFragment fragment = new ClassroomSearchFragment();
        args.putString("param",searchParam);
        fragment.setArguments(args);
        return fragment;
    }

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_classroom_search, container, false);
        mListView = (ListView) root.findViewById(R.id.classroom_results);

        String searchParam = getArguments().getString("param");
        PoliLifeDB.searchClassrooms(searchParam, this);

        return root;
    }

    @Override
    public void onClassroomsFound(final List<Classroom> result) {
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return result.size();
            }

            @Override
            public Classroom getItem(int position) {
                return result.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity())
                            .inflate(R.layout.layout_classroom_result_item, parent, false);
                }

                final Classroom item = getItem(position);
                TextView tv = (TextView) convertView.findViewById(R.id.classroom_name);
                tv.setText(item.getName());

                convertView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof ClassroomSelectionListener){
                            ((ClassroomSelectionListener) getActivity()).onClassroomSelected(item);
                        }
                    }
                });
                return convertView;
            }
        });
    }

    @Override
    public void onClassroomSearchError(Exception exception) {
        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT);
    }

}
