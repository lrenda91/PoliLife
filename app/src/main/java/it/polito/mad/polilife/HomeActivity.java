package it.polito.mad.polilife;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.parse.ParseUser;

import it.polito.mad.polilife.chat.ChatFragment;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Student;
import it.polito.mad.polilife.didactical.DidacticalHomeFragment;
import it.polito.mad.polilife.news.NewsFragment;
import it.polito.mad.polilife.noticeboard.NoticeBoardActivity;
import it.polito.mad.polilife.noticeboard.NoticeboardHomeFragment;
import it.polito.mad.polilife.placement.JobPlacementFragment;
import it.polito.mad.polilife.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private static final int PROFILE = 0;
    private static final int DIDACTICS = 1;
    private static final int NOTICEBOARD = 2;
    private static final int JOBPLACEMENT = 3;
    private static final int CHAT = 4;
    private static final int NEWS = 5;

    private Student user = (Student) ParseUser.getCurrentUser();

    private Fragment[] pages = {
            ProfileFragment.newInstance(),
            DidacticalHomeFragment.newInstance(),
            NoticeboardHomeFragment.newInstance(),
            JobPlacementFragment.newInstance(),
            ChatFragment.newInstance(),
            NewsFragment.newInstance()
    };

    private PoliLifeNavigationDrawer mNavigationDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private String[] mTitles;

    private int mCurrentFeature;
    private int currentAlpha = 255;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.getBackground().setAlpha(255);

        mTitles = getResources().getStringArray(R.array.student_drawer_titles);
        setUpNavigationDrawer();

        mNavigationDrawer.setOnItemClickListener(new PoliLifeNavigationDrawer.SimpleOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mCurrentFeature = position;
                //mNavigationDrawer.close();
                showPage(position);
            }

            @Override
            public void onFooterClick(View view) {
                PoliLifeDB.logOut(new DBCallbacks.UserLogoutCallback() {
                    @Override
                    public void onLogoutSuccess() {
                        Intent toHomePage = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(toHomePage);
                        finish();
                    }

                    @Override
                    public void onLogoutError(Exception exception) {
                        Toast.makeText(HomeActivity.this, exception.getMessage(), Toast.LENGTH_SHORT);
                        Intent toHomePage = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(toHomePage);
                        finish();
                    }
                });
            }
        });

        //by default, we'd like to see immediately all news
        showPage(CHAT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int id = R.menu.menu_empty;
        switch(mCurrentFeature){
            case NEWS:
                id = R.menu.menu_home;
                break;
        }
        getMenuInflater().inflate(id, menu);
        getSupportActionBar().setTitle(mTitles[mCurrentFeature]);
        return super.onCreateOptionsMenu(menu);
    }

    private void setUpNavigationDrawer() {
        TypedArray ar = getResources().obtainTypedArray(R.array.student_drawer_icons);
        int[] icons = new int[ar.length()];
        for (int i = 0; i < mTitles.length; i++) {
            icons[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mNavigationDrawer = new PoliLifeNavigationDrawer(
                mTitles, icons,
                mDrawerLayout
        );

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                (Toolbar) findViewById(R.id.toolbar),
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                if (i == 1) {
                    mToolbar.getBackground().setAlpha(currentAlpha);
                } else {
                    mToolbar.getBackground().setAlpha(255);
                }
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
                //showPage(mCurrentFeature);
            }

            @Override
            public void onDrawerOpened(View v) {
                if (Build.VERSION.SDK_INT >= 19) {
                    currentAlpha = mToolbar.getBackground().getAlpha();
                }
                mToolbar.getBackground().setAlpha(255);
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        /*String firstName = user.getFirstName();
        String lastName = user.getLastName();
        if (user.getPhotoFile() != null) {
            try {
                mNavigationDrawer.setUserData(firstName, lastName,
                        Utility.getBitmap(user.getPhotoFile().getData()));
            } catch (Exception e) {
                mNavigationDrawer.setUserData(firstName, lastName, R.drawable.ic_user);
            }
        } else {
            mNavigationDrawer.setUserData(firstName, lastName, R.drawable.ic_user);
        }*/

    }


    private void showPage(int position){
        FragmentManager frgManager = getSupportFragmentManager();
        frgManager.beginTransaction().replace(R.id.container, pages[position]).commit();
        mCurrentFeature = position;
        supportInvalidateOptionsMenu();
        if (mNavigationDrawer != null){
            mNavigationDrawer.close();
        }
    }

}
