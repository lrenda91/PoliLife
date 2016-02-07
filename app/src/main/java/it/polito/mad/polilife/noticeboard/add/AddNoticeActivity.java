package it.polito.mad.polilife.noticeboard.add;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Notice;
import it.polito.mad.polilife.db.parcel.PNoticeData;
import it.polito.mad.polilife.noticeboard.NoticeBoardActivity;
import it.polito.mad.polilife.noticeboard.NoticeUpdater;


public class AddNoticeActivity extends AppCompatActivity
        implements DBCallbacks.UpdateCallback<Notice> {

    private PNoticeData mWrapper = new PNoticeData();

    private String mNewNoticeType;

    private ViewPager mViewPager;
    private int mCurrentPage = 0;
    private Fragment[] pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);
        mNewNoticeType = getIntent().getStringExtra(NoticeBoardActivity.TYPE_EXTRA_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(mNewNoticeType.equals(Notice.HOME_TYPE) ?
            R.string.title_add_home_notice : R.string.title_add_book_notice);

        pages = new Fragment[]{
                Page1.newInstance(mNewNoticeType), new Page2()
        };

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                supportInvalidateOptionsMenu();
            }
        });
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pages[position];
            }

            @Override
            public int getCount() {
                return pages.length;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuID = (mCurrentPage == 0) ? R.menu.menu_forward : R.menu.menu_new_notice;
        getMenuInflater().inflate(menuID, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_forward:
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.action_create:
                for (Fragment page : pages){
                    if (page instanceof NoticeUpdater){
                        ((NoticeUpdater) page).update(mWrapper);
                    }
                }
                if (mNewNoticeType.equals(Notice.HOME_TYPE)) {
                    PoliLifeDB.publishNewHomeNotice(mWrapper, this);
                }
                else if (mNewNoticeType.equals(Notice.BOOK_TYPE)){
                    PoliLifeDB.publishNewBookNotice(mWrapper, this);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateError(Exception exception) {
        Toast.makeText(this, "error while publishing notice", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateSuccess(Notice newNotice) {
        Intent backIntent = new Intent();
        setResult(Activity.RESULT_OK, backIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, backIntent);
        finish();
    }

}