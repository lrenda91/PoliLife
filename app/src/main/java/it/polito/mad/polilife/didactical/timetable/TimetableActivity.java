package it.polito.mad.polilife.didactical.timetable;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.didactical.timetable.data.*;

public class TimetableActivity extends AppCompatActivity {

    private static final int NUM_DAYS = 5;

    private static final int DAY_VIEW = 1;
    private static final int WEEK_VIEW = 2;

    private ViewFlipper mFlipper;
    private Toolbar mToolbar;

    //private DayFragment[] dayPages;
    private View[] dayPages;

    private Timetable data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFlipper = (ViewFlipper) findViewById(R.id.flipper);

        //data = (Timetable) getIntent().getSerializableExtra("model");
        if (savedInstanceState == null){
            InputStream lecturesStream = null, coursesStream = null;
            try {
                lecturesStream = getAssets().open("timetable.json");
                coursesStream = getAssets().open("courses.json");
            } catch (IOException e) {}
            data = TimetableImpl.newInstance(coursesStream, lecturesStream);
        }
        else{
            data = (Timetable) savedInstanceState.getSerializable("model");
        }


        dayPages = new View[NUM_DAYS];
        for (int i=0;i<NUM_DAYS;i++){
            dayPages[i] = LayoutInflater.from(this).inflate(R.layout.dayview, null);
        }
        //dayPages = new DayFragment[5];
        int[] dayLayoutIDs = {
                R.id.mondayRelativeLayout,
                R.id.tuesdayRelativeLayout,
                R.id.wednesdayRelativeLayout,
                R.id.thursdayRelativeLayout,
                R.id.fridayRelativeLayout
        };

        Collection<Lecture> lectures = data.filter(null,null);
        for (Lecture lecture : lectures){
            int i = lecture.getDayOfWeek();
            //setup day mode
            if (dayPages[i] == null){
                //dayPages[i] = DayFragment.newInstance(i);
                dayPages[i] = LayoutInflater.from(TimetableActivity.this).inflate(R.layout.dayview, null);
            }
            if (dayPages[i] instanceof ViewGroup){
                ((ViewGroup) dayPages[i]).addView(Utility.getView(this, lecture));
            }
            //setup week mode
            ViewGroup vg = (ViewGroup) findViewById(dayLayoutIDs[i]);
            vg.addView(Utility.getView(this, lecture));
        }

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        final String[] titles = getResources().getStringArray(R.array.days);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return NUM_DAYS;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //View page = LayoutInflater.from(TimetableActivity.this).inflate(R.layout.dayview, container, false);
                View page = dayPages[position];
                container.addView(page);
                return page;
                //return super.instantiateItem(container, position);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        ViewPager.OnPageChangeListener pageChangeListener =
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        mToolbar.setTitle(mViewPager.getAdapter().getPageTitle(position));
                    }
                };
        mViewPager.addOnPageChangeListener(pageChangeListener);

        int curDayCalendar = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int curDayIdx;
        switch (curDayCalendar){
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                curDayIdx = 0;
                break;
            default: curDayIdx = curDayCalendar - 2;
        }
        mViewPager.setCurrentItem(curDayIdx);
        pageChangeListener.onPageSelected(curDayIdx);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuID = 0;
        switch (mFlipper.getCurrentView().getId()){
            case R.id.day_view:
                menuID = R.menu.menu_timetable_day;
                break;
            case R.id.week_view:
                menuID = R.menu.menu_timetable_week;
                break;
        }
        getMenuInflater().inflate(menuID, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mFlipper.showNext();
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putSerializable("model", data);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            data = (Timetable) savedInstanceState.getSerializable("model");
        }
    }

}
