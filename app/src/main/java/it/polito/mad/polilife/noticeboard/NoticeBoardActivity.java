package it.polito.mad.polilife.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Notice;

public class NoticeBoardActivity extends AppCompatActivity
        implements DBCallbacks.GetListCallback<Notice>, MyNoticesFragment.NoticeDeleteListener {

    public static final int ADVANCED_SEARCH_REQUEST_CODE = 10;
    public static final int PUBLISH_NEW_NOTICE_REQUEST_CODE = 3;

    private Toolbar mToolbar;
    private ViewPager mViewPager;

    public static final String TYPE_EXTRA_KEY = "TYPE";
    public static final String BOOK_TYPE = Notice.BOOK_TYPE;
    public static final String HOME_TYPE = Notice.HOME_TYPE;

    private String mNoticesType;
    private List<Notice> mNotices = new LinkedList<>();

    private ProgressBar mWait;
    private Fragment[] mSections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        mNoticesType = getIntent().getStringExtra(NoticeBoardActivity.TYPE_EXTRA_KEY);
        mSections = new Fragment[]{
                AllNoticesFragment.newInstance(mNoticesType),
                MyNoticesFragment.newInstance(mNoticesType)
        };
        final String[] mTitles = {
                getString(R.string.all_notices),
                getString(R.string.my_notices)
        };

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            public Fragment getItem(int position) {
                return mSections[position];
            }
            @Override
            public int getCount() {
                return mSections.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.pin);
        mTabLayout.getTabAt(1).setIcon(R.drawable.pin);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWait = (ProgressBar) findViewById(R.id.wait);
        mWait.setVisibility(View.VISIBLE);

        Notice.Filter filter = new Notice.Filter(mNoticesType);

        //boolean fromLocalDataStore = !Utility.networkIsUp(this);
        PoliLifeDB.advancedNoticeFilter(filter, this);
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
                Class<? extends Activity> target = mNoticesType.equals(HOME_TYPE) ?
                        HouseSearchActivity.class : BookSearchActivity.class;
                Intent toAdvSearchPage = new Intent(NoticeBoardActivity.this, target);
                startActivityForResult(toAdvSearchPage, ADVANCED_SEARCH_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFetchSuccess(List<Notice> result) {
        mNotices = result;
        mWait.setVisibility(View.INVISIBLE);
        for (Fragment f : mSections){
            if (f instanceof NoticesListener){
                ((NoticesListener) f).update(result);
            }
        }
    }

    @Override
    public void onFetchError(Exception exception) {
        mWait.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNoticeDeleted(Notice notice) {
        mNotices.remove(notice);
        for (Fragment f : mSections){
            if (f instanceof NoticesListener){
                ((NoticesListener) f).update(mNotices);
            }
        }
        Toast.makeText(this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADVANCED_SEARCH_REQUEST_CODE){
            switch(resultCode){
                case Activity.RESULT_OK:
                    //boolean fromLocalDataStore = !Utility.networkIsUp(this);
                    Notice.Filter searchParams = (Notice.Filter) data.getSerializableExtra("params");
                    PoliLifeDB.advancedNoticeFilter(searchParams, this);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
        else if (requestCode == PUBLISH_NEW_NOTICE_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                PoliLifeDB.advancedNoticeFilter(new Notice.Filter(mNoticesType), this);
            }
        }
    }

}
