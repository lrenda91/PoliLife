package it.polito.mad.polilife.didactical.timetable;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.didactical.timetable.data.*;

public class TimetableActivity extends AppCompatActivity {

    private static final int DAY_VIEW = 1;
    private static final int WEEK_VIEW = 2;

    private ViewFlipper mFlipper;
    private Toolbar mToolbar;

    private DayFragment[] dayPages;

    private Timetable data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFlipper = (ViewFlipper) findViewById(R.id.flipper);

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


        dayPages = new DayFragment[5];
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
                dayPages[i] = DayFragment.newInstance(i);
            }
            HashSet<Lecture> hs = (HashSet<Lecture>) dayPages[i].getArguments().getSerializable("lectures");
            hs.add(lecture);

            //setup week mode
            ViewGroup vg = (ViewGroup) findViewById(dayLayoutIDs[i]);
            vg.addView(Utility.getView(this, lecture));
        }

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        DayViewPagerAdapter adapter = new DayViewPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        ViewPager.OnPageChangeListener pageChangeListener =
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        mToolbar.setTitle(mViewPager.getAdapter().getPageTitle(position));
                    }
                };
        mViewPager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(0);
        //TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        //mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                mFlipper.showNext();
                break;
        }
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

    public static class DayFragment extends Fragment {
        static DayFragment newInstance(int day){
            Bundle args = new Bundle();
            args.putInt("day", day);
            args.putSerializable("lectures", new HashSet<Lecture>());
            DayFragment df = new DayFragment();
            df.setArguments(args);
            return df;
        }
        private ViewGroup root;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            root = (ViewGroup) inflater.inflate(R.layout.dayview, container, false);
            int day = getArguments().getInt("day");
            HashSet<Lecture> lectures = (HashSet<Lecture>) getArguments().getSerializable("lectures");
            for (Lecture l : lectures){
                root.addView(Utility.getView(getActivity(),l));
            }
            return root;
        }

    }


    public class DayViewPagerAdapter extends FragmentPagerAdapter {

        //private DayFragment[] sections;
        private String[] titles;

        public DayViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            titles = context.getResources().getStringArray(R.array.days);
        }

        @Override
        public Fragment getItem(int position) {
            return dayPages[position];
        }

        @Override
        public int getCount() {
            return dayPages.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

}
