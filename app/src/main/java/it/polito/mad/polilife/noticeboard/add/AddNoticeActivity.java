package it.polito.mad.polilife.noticeboard.add;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.classes.Notice;
import it.polito.mad.polilife.db.parcel.PNoticeData;


public class AddNoticeActivity extends AppCompatActivity
        implements View.OnClickListener, DBCallbacks.UpdateCallback<Notice> {

    private PNoticeData data = new PNoticeData();

    private ViewPager mViewPager;
    private int mCurrentPage = 0;
    private Fragment[] pages = new Fragment[]{
            new Page1(), new Page2()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPage = position;
                if (position == 0){
                    findViewById(R.id.previous_page_arrow).setVisibility(View.INVISIBLE);
                }
                else if (position == mViewPager.getAdapter().getCount()-1){
                    findViewById(R.id.next_page_arrow).setVisibility(View.INVISIBLE);
                }
                else{
                    findViewById(R.id.previous_page_arrow).setVisibility(View.VISIBLE);
                    findViewById(R.id.next_page_arrow).setVisibility(View.VISIBLE);
                }
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
        findViewById(R.id.previous_page_arrow).setOnClickListener(this);
        findViewById(R.id.next_page_arrow).setOnClickListener(this);

    }

    @Override
    public void onUpdateError(Exception exception) {
        Toast.makeText(this, "error while publishing notice", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateSuccess(Notice newNotice) {
        //PNoticeData wrapper = new PNoticeData();
        //wrapper.fillFrom(newNotice);
        Intent backIntent = new Intent();
        //backIntent.putExtra("notice", wrapper);
        setResult(Activity.RESULT_OK, backIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, backIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previous_page_arrow:
                mViewPager.setCurrentItem(mCurrentPage-1);
                break;
            case R.id.next_page_arrow:
                mViewPager.setCurrentItem(mCurrentPage+1);
                break;
        }
    }

}