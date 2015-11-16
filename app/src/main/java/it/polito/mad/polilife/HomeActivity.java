package it.polito.mad.polilife;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Student;
import it.polito.mad.polilife.didactical.DidacticalHomeFragment;
import it.polito.mad.polilife.didactical.rooms.ClassroomSearchFragment;
import it.polito.mad.polilife.didactical.timetable.LectureDetailsActivity;

public class HomeActivity extends AppCompatActivity {

    private Student user = (Student) ParseUser.getCurrentUser();

    //private SectionsPagerAdapter mSectionsPagerAdapter;

    //private ViewPager mViewPager;

    private PoliLifeNavigationDrawer mNavigationDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private int currentAlpha = 255;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(255);

        setUpNavigationDrawer();

        mNavigationDrawer.setOnItemClickListener(new PoliLifeNavigationDrawer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Fragment f = DidacticalHomeFragment.newInstance();
                FragmentManager frgManager = getSupportFragmentManager();
                frgManager.beginTransaction().replace(R.id.container, f).commit();
                mNavigationDrawer.toggle();
            }

            @Override
            public void onHeaderClick(View view) {
                Intent intent = new Intent(view.getContext(), LectureDetailsActivity.class);
                intent.putExtra("course", "aaaaa");
                intent.putExtra("teacher", "bbbbbb");
                intent.putExtra("time", "08:30");
                intent.putExtra("room", "7I");
                intent.putExtra("color", R.color.brown);
                view.getContext().startActivity(intent);
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

    }

    private void setUpNavigationDrawer() {
        String[] titles = getResources().getStringArray(R.array.student_drawer_titles);
        TypedArray ar = getResources().obtainTypedArray(R.array.student_drawer_icons);
        int[] icons = new int[ar.length()];
        for (int i = 0; i < titles.length; i++) {
            icons[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mNavigationDrawer = new PoliLifeNavigationDrawer(
                titles, icons,
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
                    toolbar.getBackground().setAlpha(currentAlpha);
                } else {
                    toolbar.getBackground().setAlpha(255);
                }
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerOpened(View v) {
                if (Build.VERSION.SDK_INT >= 19) {
                    currentAlpha = toolbar.getBackground().getAlpha();
                }
                toolbar.getBackground().setAlpha(255);
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
/*
    @Override
    public void onScrolled(int dx, int dy) {
        //height of the header image and the toolbar
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int headerHeight = (int) getResources().getDimension(R.dimen.header_image_height);
        int toolbarHeight = (int) getResources().getDimension(R.dimen.toolbar_height);


        //difference between the heights in pixel
        float heightPx = (headerHeight - toolbarHeight);

        //value of the transparency(between 0 and 255) based on the scroll displacement
        int transparency = (int) (dy * (256 / heightPx));

        if (transparency < 256) {
            toolbar.getBackground().setAlpha(transparency);
            currentAlpha = transparency;
            System.out.println("TRA " + transparency + " dy " + dy + " px" + heightPx);

        } else {
            toolbar.getBackground().setAlpha(255);
            currentAlpha = 255;
        }
    }

*/
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] sections = {
                DidacticalHomeFragment.newInstance(),
                DidacticalHomeFragment.newInstance(),
                DidacticalHomeFragment.newInstance()
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
                R.drawable.ic_pause_dark,
                R.drawable.ic_play_dark
        };

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = "SECTION 1"; break;
                case 1:
                    title = "SECTION 2"; break;
                case 2:
                    title = "SECTION 3"; break;
            }
            Drawable image = ContextCompat.getDrawable(HomeActivity.this, imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(title);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

    }

}
