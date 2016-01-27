package it.polito.mad.polilife.placement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Job;

public class JobsSearchActivity extends AppCompatActivity
        implements DBCallbacks.GetListCallback<Job> {

    private TextView mResultCount;
    private ListView mOffersListView;
    private PositionsBaseAdapter mOffersAdapter = new PositionsBaseAdapter(this);

    private Job.Filter mFilterParams = new Job.Filter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_search);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ((ListView) findViewById(R.id.jobs_list)).setAdapter(mOffersAdapter);
        mResultCount = (TextView) findViewById(R.id.result_count);
        mOffersAdapter.setOnClickListener(new PositionsBaseAdapter.OnJobClickListener() {
            @Override
            public void onPositionItemClick(View itemView, int position, Job item) {
                Intent intent = new Intent(JobsSearchActivity.this, PositionDetailsActivity.class);
                intent.putExtra("id", item.getObjectId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_search:
                new JobSearchDialog(this).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFetchSuccess(List<Job> result) {
        mOffersAdapter.setData(result);
        mOffersAdapter.notifyDataSetChanged();
        mResultCount.setText(result.size() + " " + getResources().getString(R.string.count_result));
    }

    @Override
    public void onFetchError(Exception exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    class JobSearchDialog extends Dialog {
        private Spinner tocSpinner, todSpinner;
        private EditText nameEditText, cityEditText;
        private DatePicker fromPicker;
        public JobSearchDialog(Context context){
            super(context);
            setContentView(R.layout.layout_dialog_job_search);
            tocSpinner = (Spinner) findViewById(R.id.typeofcontract_spinner);
            todSpinner = (Spinner) findViewById(R.id.typeofdegree_spinner);
            nameEditText = (EditText) findViewById(R.id.name_et);
            cityEditText = (EditText) findViewById(R.id.city_et);
            fromPicker = (DatePicker) findViewById(R.id.datePicker);
            setTitle("Custom Dialog");
            findViewById(R.id.btn_fromdate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromPicker.getVisibility() == View.GONE) {
                        fromPicker.setVisibility(View.VISIBLE);
                    } else {
                        fromPicker.setVisibility(View.GONE);
                    }
                }
            });
            findViewById(R.id.filter_button).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fillParams();
                            PoliLifeDB.advancedJobsFilter(mFilterParams, false, JobsSearchActivity.this);
                            dismiss();
                        }
                    });
            findViewById(R.id.dismiss_button).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
            Resources res = getResources();
            Utility.setupSpinnerWithHint(getContext(), tocSpinner,
                    res.getStringArray(R.array.types_of_contract),
                    res.getString(R.string.job_contract_hint));
            Utility.setupSpinnerWithHint(getContext(), todSpinner,
                    res.getStringArray(R.array.required_career_arrays),
                    res.getString(R.string.job_degree_hint));
        }

        private void fillParams(){
            String n = nameEditText.getText().toString().isEmpty() ? null :
                    nameEditText.getText().toString();
            String c = cityEditText.getText().toString().isEmpty() ? null :
                    cityEditText.getText().toString();
            String toc = tocSpinner.getSelectedItemPosition() == 0 ? null :
                    tocSpinner.getSelectedItem().toString();
            String tod = todSpinner.getSelectedItemPosition() == 0 ? null :
                    todSpinner.getSelectedItem().toString();
            Calendar cal = Calendar.getInstance();
            cal.set(
                    fromPicker.getYear(),
                    fromPicker.getMonth(),
                    fromPicker.getDayOfMonth()
            );
            mFilterParams.name(n)
                    .typeOfContract(toc)
                    .city(c)
                    .typeOfDegree(tod)
                    .startDate(fromPicker.getVisibility() == View.GONE ? null :
                                   cal.getTime()
                );
        }

    }



}
