package it.polito.mad.polilife.didactical.rooms;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Classroom;
import it.polito.mad.polilife.didactical.ClassroomSelectionListener;

public class ClassroomActivity extends AppCompatActivity
        implements ClassroomSelectionListener {

    private Toolbar mToolbar;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    private void showFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.classroom_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_search);

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

        /*final EditText et = (EditText) findViewById(R.id.search_edit_text);
        findViewById(R.id.search_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = et.getText().toString();
                Fragment results = ClassroomSearchFragment.newInstance(param);
                showFragment(results);
            }
        });*/
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar
        final InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(isSearchOpened){ //test if the search is open
            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar
            //hides the keyboard
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);
            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_action_search));
            isSearchOpened = false;
        }
        else {
            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.layout_search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            edtSeach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
                }
            });
            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);
                        doSearch(edtSeach.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_clear_grey600_24dp));
            isSearchOpened = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }


    private void doSearch(String param) {

        Fragment results = ClassroomSearchFragment.newInstance(param);
        showFragment(results);
    }



    @Override
    public void onClassroomSelected(Classroom classroom) {
        Fragment fragment = ClassroomDetailsFragment.newInstance(
                classroom.getName(),
                classroom.getLocation().getLatitude(),
                classroom.getLocation().getLongitude(),
                classroom.getDetails()
        );
        showFragment(fragment);
    }

}
