package it.polito.mad.polilife.placement;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polito.mad.polilife.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobPlacementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobPlacementFragment extends Fragment {

    public JobPlacementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment JobPlacementFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_placement, container, false);
    }

}
