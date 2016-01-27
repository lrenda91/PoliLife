package it.polito.mad.polilife.placement;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.polito.mad.polilife.R;

public class JobPlacementFragment extends Fragment {

    public interface Listener {
        void onSelectAppliedJobs();
        void onSelectSavedJobs();
        void onProfileSelected();
    }

    private Button mSearchBtn, mSavedBtn, mAppliedBtn;

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
        Button profileButton = (Button) view.findViewById(R.id.profile_button);
        Button searchButton = (Button) view.findViewById(R.id.search_jobs);
        Button appliedButton = (Button) view.findViewById(R.id.applied_button);
        Button savedButton = (Button) view.findViewById(R.id.saved_jobs_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof Listener){
                    ((Listener) getActivity()).onProfileSelected();
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), JobsSearchActivity.class));
            }
        });

        appliedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof Listener){
                    ((Listener) getActivity()).onSelectAppliedJobs();
                }
            }
        });

        savedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof Listener){
                    ((Listener) getActivity()).onSelectSavedJobs();
                }
            }
        });
    }

}
