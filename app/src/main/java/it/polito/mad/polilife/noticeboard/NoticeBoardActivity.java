package it.polito.mad.polilife.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Notice;
import it.polito.mad.polilife.db.parcel.PNoticeData;
import it.polito.mad.polilife.didactical.DidacticalHomeFragment;

public class NoticeBoardActivity extends AppCompatActivity
        implements DBCallbacks.FilterCallback<Notice> {

    private static final int ADVANCED_SEARCH_REQUEST_CODE = 10;

    private Toolbar mToolbar;
    private ViewPager mViewPager;

    private ProgressBar waitPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        waitPB = (ProgressBar) findViewById(R.id.wait);
        waitPB.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noticeboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_search_notices:
                Intent toAdvSearchPage = new Intent(NoticeBoardActivity.this, AdvancedSearchActivity.class);
                startActivityForResult(toAdvSearchPage, ADVANCED_SEARCH_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADVANCED_SEARCH_REQUEST_CODE){
            switch(resultCode){
                case Activity.RESULT_OK:
                    if (!Utility.networkIsUp(this)){
                        return;
                    }
                    Notice.FilterData searchParams = (Notice.FilterData) data.getSerializableExtra("params");
                    PoliLifeDB.advancedNoticeFilter(searchParams, this);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public void onDataFiltered(List<Notice> result) {
        List<PNoticeData> list = new LinkedList<>();
        for (Notice n : result){
            PNoticeData pNoticeData = new PNoticeData();
            pNoticeData.fillFrom(n);
            list.add(pNoticeData);
        }
        /*for (Fragment f : fragments) {
            if (f instanceof NoticesListener) {
                ((NoticesListener) f).update(list);
            }
        }*/
        waitPB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFilterError(Exception exception) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] sections = {
                AllNoticesFragment.newInstance(),
                MyNoticesFragment.newInstance("","")
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return sections[position];
        }

        @Override
        public int getCount() {
            return sections.length;
        }

        private int[] imageResId = {
                R.drawable.ic_cast_dark,
                R.drawable.ic_pause_dark
        };

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = "All"; break;
                case 1:
                    title = "My notices"; break;
            }
            Drawable image = ContextCompat.getDrawable(NoticeBoardActivity.this, imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(title);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

    }

}
