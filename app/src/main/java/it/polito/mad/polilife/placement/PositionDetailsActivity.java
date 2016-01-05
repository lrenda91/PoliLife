package it.polito.mad.polilife.placement;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Company;
import it.polito.mad.polilife.db.classes.Position;

public class PositionDetailsActivity extends AppCompatActivity {

    private ViewFlipper mViewFlipper;
    private int mCurrentIdx = 0;

    private View page1, page2;
    private View aboutLayout, dateLayout, locationLayout, tojLayout, todLayout;
    private ProgressBar mPositionProgressBar, mCompanyProgressBar;

    private DBCallbacks.SingleFetchCallback<Position> mPositionFetchCallback =
            new DBCallbacks.SingleFetchCallback<Position>() {
        @Override
        public void onFetchSuccess(Position result) {
            Resources res = getResources();
            DateFormat df = new SimpleDateFormat(res.getString(R.string.date_format));
            mPositionProgressBar.setVisibility(View.INVISIBLE);
            String about = result.getAbout() != null ? result.getAbout() : res.getString(R.string.no_about);
            String from = result.getStartDate() != null ?
                    df.format(result.getStartDate()) : res.getString(R.string.no_start_date);
            String location =
                    result.getCity() != null ? result.getCity() : res.getString(R.string.no_city)
                            + ", " +
                            result.getCountry() != null ? result.getCountry() : res.getString(R.string.no_country);
            String toj = result.getTypeOfJob() != null ? result.getTypeOfJob() : res.getString(R.string.unspecified);
            String tod = result.getTypeOfDegree() != null ? result.getTypeOfDegree() : res.getString(R.string.unspecified);
            ((TextView) aboutLayout.findViewById(R.id.rowText)).setText(about);
            ((TextView) dateLayout.findViewById(R.id.rowText)).setText(from);
            ((TextView) locationLayout.findViewById(R.id.rowText)).setText(location);
            ((TextView) tojLayout.findViewById(R.id.rowText)).setText(toj);
            ((TextView) todLayout.findViewById(R.id.rowText)).setText(tod);

            Company c = result.getParentCompany();
            if (c == null){
                return;
            }
            PoliLifeDB.retrieveObject(c.getObjectId(), Company.class, true, mCompanyFetchCallback);
        }

        @Override
        public void onFetchError(Exception exception) {

        }
    };

    private DBCallbacks.SingleFetchCallback<Company> mCompanyFetchCallback = new DBCallbacks.SingleFetchCallback<Company>() {
        @Override
        public void onFetchSuccess(Company result) {
            mCompanyProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFetchError(Exception exception) {
            String s = exception.getMessage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_details);

        mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        page1 = mViewFlipper.getChildAt(0);
        page2 = mViewFlipper.getChildAt(1);

        aboutLayout = findViewById(R.id.position_about_layout);
        dateLayout = findViewById(R.id.position_date_layout);
        locationLayout = findViewById(R.id.position_location_layout);
        tojLayout = findViewById(R.id.position_type_of_job_layout);
        todLayout = findViewById(R.id.position_type_of_degree_layout);
        mPositionProgressBar = (ProgressBar) page1.findViewById(R.id.wait);
        mCompanyProgressBar = (ProgressBar) page2.findViewById(R.id.wait);

        final Context ctx = this;
        findViewById(R.id.go_to_company_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.setInAnimation(ctx, R.anim.view_transition_in_left);
                mViewFlipper.setOutAnimation(ctx, R.anim.view_transition_out_left);
                mViewFlipper.showNext();
                mCurrentIdx = 1;
            }
        });

        String positionID = getIntent().getStringExtra("id");
        PoliLifeDB.retrieveObject(positionID, Position.class, true, mPositionFetchCallback);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentIdx == 0) {
            super.onBackPressed();
        }
        else{
            mViewFlipper.setInAnimation(this, R.anim.view_transition_in_right);
            mViewFlipper.setOutAnimation(this, R.anim.view_transition_out_right);
            mViewFlipper.showPrevious();
            mCurrentIdx = 0;
        }
    }
}
