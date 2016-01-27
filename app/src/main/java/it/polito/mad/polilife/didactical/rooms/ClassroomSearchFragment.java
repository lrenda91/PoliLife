package it.polito.mad.polilife.didactical.rooms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Classroom;
import it.polito.mad.polilife.didactical.ClassroomSelectionListener;

/**
 * Created by luigi onSelectAppliedJobs 12/11/15.
 */
public class ClassroomSearchFragment extends Fragment
        implements DBCallbacks.GetListCallback<Classroom> {

    public static ClassroomSearchFragment newInstance(String searchParam){
        Bundle args = new Bundle();
        ClassroomSearchFragment fragment = new ClassroomSearchFragment();
        args.putString("param",searchParam);
        fragment.setArguments(args);
        return fragment;
    }

    private static final int WAITING = 0;
    private static final int NO_RESULTS = 1;
    private static final int LIST = 2;

    private ViewFlipper mViewFlipper;
    private ListView mListView;

    private void show(int curViewIdx){
        mViewFlipper.setDisplayedChild(curViewIdx);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classroom_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewFlipper = (ViewFlipper) view.findViewById(R.id.flipper);
        mListView = (ListView) view.findViewById(R.id.classroom_results);
        String searchParam = getArguments().getString("param");
        PoliLifeDB.searchClassrooms(searchParam, false, this);
        show(WAITING);
    }

    @Override
    public void onFetchSuccess(final List<Classroom> result) {
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
        show(result.isEmpty() ? NO_RESULTS : LIST);
    }

    @Override
    public void onFetchError(Exception exception) {
        show(NO_RESULTS);
    }

}
