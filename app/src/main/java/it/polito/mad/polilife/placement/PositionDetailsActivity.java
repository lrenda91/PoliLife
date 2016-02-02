package it.polito.mad.polilife.placement;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Company;
import it.polito.mad.polilife.db.classes.Job;
import it.polito.mad.polilife.db.classes.StudentInfo;

public class PositionDetailsActivity extends AppCompatActivity {

    private ViewFlipper mViewFlipper;
    private int mCurrentIdx = 0;

    private View page1, page2;
    private View mPosAbout, mPosDate, mPosLocation, mPosTOContract, mPosTODegree;
    private View mCompAbout, mCompLocation, mCompWebPage;
    private ProgressBar mPositionProgressBar, mCompanyProgressBar;
    private ImageView mCompanyLogo;

    private DBCallbacks.GetOneCallback<Job> mPositionFetchCallback =
            new DBCallbacks.GetOneCallback<Job>() {
        @Override
        public void onFetchSuccess(final Job result) {
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
            String toc = result.getTypeOfContract() != null ? result.getTypeOfContract() : res.getString(R.string.unspecified);
            String tod = result.getTypeOfDegree() != null ? result.getTypeOfDegree() : res.getString(R.string.unspecified);
            ((TextView) mPosAbout.findViewById(R.id.rowText)).setText(about);
            ((TextView) mPosDate.findViewById(R.id.rowText)).setText(from);
            ((TextView) mPosLocation.findViewById(R.id.rowText)).setText(location);
            ((TextView) mPosTOContract.findViewById(R.id.rowText)).setText(toc);
            ((TextView) mPosTODegree.findViewById(R.id.rowText)).setText(tod);

            findViewById(R.id.apply_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PoliLifeDB.apply(result, new DBCallbacks.UpdateCallback<StudentInfo>() {
                        @Override
                        public void onUpdateSuccess(StudentInfo updated) {
                            Toast.makeText(PositionDetailsActivity.this, "Applied", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onUpdateError(Exception exception) {
                            Toast.makeText(PositionDetailsActivity.this, "Error applying", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            Company c = result.getParentCompany();
            if (c == null){
                return;
            }
            PoliLifeDB.retrieveObject(c.getObjectId(), Company.class,
                    new DBCallbacks.GetOneCallback<Company>() {
                @Override
                public void onFetchSuccess(Company result) {
                    mCompanyProgressBar.setVisibility(View.INVISIBLE);
                    Resources res = getResources();
                    String about = result.getAbout() != null ? result.getAbout() : res.getString(R.string.no_about);
                    String location =
                            result.getCity() != null ?
                                    result.getCity() : res.getString(R.string.no_city)
                                    + ", " +
                                    result.getCountry() != null ? result.getCountry() : res.getString(R.string.no_country);
                    String webPage = result.getWebPage() != null ?
                            result.getWebPage() : res.getString(R.string.no_web_page);
                    ((TextView) mCompAbout.findViewById(R.id.rowText)).setText(about);
                    ((TextView) mCompLocation.findViewById(R.id.rowText)).setText(location);
                    ((TextView) mCompWebPage.findViewById(R.id.rowText)).setText(webPage);

                    ParseFile logo = result.getLogo();
                    try {
                        if (logo != null && logo.getData() != null){
                            byte[] data = logo.getData();
                            mCompanyLogo.setImageBitmap(
                                    BitmapFactory.decodeByteArray(data, 0, data.length));
                        }
                        else mCompanyLogo.setImageResource(R.drawable.student_icon);
                    }catch(ParseException e){
                        mCompanyLogo.setImageResource(R.drawable.student_icon);
                    }
                }

                @Override
                public void onFetchError(Exception exception) {
                    mCompanyProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void onFetchError(Exception exception) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_details);

        mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        page1 = mViewFlipper.getChildAt(0);
        page2 = mViewFlipper.getChildAt(1);

        mPosAbout = findViewById(R.id.position_about_layout);
        mPosDate = findViewById(R.id.position_date_layout);
        mPosLocation = findViewById(R.id.position_location_layout);
        mPosTOContract = findViewById(R.id.position_type_of_job_layout);
        mPosTODegree = findViewById(R.id.position_type_of_degree_layout);
        mPositionProgressBar = (ProgressBar) page1.findViewById(R.id.wait);

        mCompanyLogo = (ImageView) findViewById(R.id.company_logo);
        mCompAbout = findViewById(R.id.company_about_layout);
        mCompLocation = findViewById(R.id.company_location_layout);
        mCompWebPage = findViewById(R.id.company_web_page_layout);
        mCompanyProgressBar = (ProgressBar) page2.findViewById(R.id.wait);

        final Context ctx = this;
        findViewById(R.id.go_to_company_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.setInAnimation(ctx, R.anim.slide_in_left);
                mViewFlipper.setOutAnimation(ctx, R.anim.slide_out_left);
                mViewFlipper.showNext();
                mCurrentIdx = 1;
            }
        });

        String positionID = getIntent().getStringExtra("id");
        PoliLifeDB.retrieveObject(positionID, Job.class, mPositionFetchCallback);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentIdx == 0) {
            super.onBackPressed();
        }
        else{
            mViewFlipper.setInAnimation(this, R.anim.slide_in_right);
            mViewFlipper.setOutAnimation(this, R.anim.slide_out_right);
            mViewFlipper.showPrevious();
            mCurrentIdx = 0;
        }
    }
}
