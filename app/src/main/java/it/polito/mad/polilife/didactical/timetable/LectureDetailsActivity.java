package it.polito.mad.polilife.didactical.timetable;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Classroom;
import it.polito.mad.polilife.didactical.timetable.data.Lecture;

public class LectureDetailsActivity extends AppCompatActivity
        implements DBCallbacks.GetListCallback<Classroom>, OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mToolbar;
    private Classroom mClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle extras = getIntent().getExtras();
        final Lecture data = (Lecture) extras.getSerializable("lecture");

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, extras.getInt("color")));


        if (data.getCourse() != null && data.getCourse().getName() != null) {
            ((TextView) findViewById(R.id.courseTextView)).setText(data.getCourse().getName());
        }

        if (data.getCourse() != null && data.getCourse().getProfessor() != null &&
                data.getCourse().getProfessor().getName() != null){
            ((TextView) findViewById(R.id.professor_name)).setText(data.getCourse().getProfessor().getName());
            ((TextView) findViewById(R.id.professor_office)).setText(data.getCourse().getProfessor().getOffice());
            /*item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View details = v.findViewById(R.id.prof_details_layout);
                    if (details.getVisibility() == View.GONE) {
                        expand(details, details);
                    } else {
                        collapse(details, details);
                    }
                }
            });*/
        }
        if (data.getClassroom() != null){
            View item = findViewById(R.id.lecture_classroom_name);
            ((ImageView) item.findViewById(R.id.rowIcon)).setImageResource(R.drawable.ic_mail);
            ((TextView) item.findViewById(R.id.rowText)).setText(data.getClassroom());
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PoliLifeDB.searchClassrooms(data.getClassroom(), true, LectureDetailsActivity.this);
                }
            });
        }
        if (data.getStartTime() != null && data.getEndTime() != null){
            View item = findViewById(R.id.lecture_schedule);
            ((ImageView) item.findViewById(R.id.rowIcon)).setImageResource(R.drawable.ic_mail);
            ((TextView) item.findViewById(R.id.rowText)).setText(
                    getResources().getStringArray(R.array.days)[data.getDayOfWeek()]
                    + " - " + data.getStartTime() + " - " + data.getEndTime());
        }

    }

    @Override
    public void onFetchSuccess(List<Classroom> result) {
        if (result.size() == 0){
            return;
        }
        mClassroom = result.get(0);
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map_container, mapFragment); // map_container is your FrameLayout container
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.addToBackStack(null);
        ft.commit();
        mapFragment.getMapAsync(LectureDetailsActivity.this);
    }

    @Override
    public void onFetchError(Exception exception) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String name = mClassroom.getName();
        double latitude = mClassroom.getLocation().getLatitude();
        double longitude = mClassroom.getLocation().getLongitude();

        LatLng room = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(room).title(name));

        float zoom = Utility.calculateZoomLevel(this, 2000);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(room, zoom));
    }
/*
    private void expand(View view, View parent) {
        //set Visible
        view.setVisibility(View.VISIBLE);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.AT_MOST);
        view.measure(widthSpec, heightSpec);
        int h  = view.getMeasuredHeight();
        Animator mAnimator = slideAnimator(0, h, view);
        mAnimator.start();
    }

    private void collapse(final View view, View parent) {
        int finalHeight = view.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, view);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final View summary) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
*/
}
