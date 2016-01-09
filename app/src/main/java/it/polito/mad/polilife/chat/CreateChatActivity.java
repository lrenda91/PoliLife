package it.polito.mad.polilife.chat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;

public class CreateChatActivity extends AppCompatActivity
    implements DBCallbacks.MultipleFetchCallback<ParseUser> {

    public static final int ONE_TO_ONE = 0;
    public static final int ONE_TO_MANY = 1;

    private int mMode;
    //private ListView mListView;

    //only in 1 to 1 mode
    private List<ParseUser> mAllUsers = new ArrayList<>();

    //only in 1 to N mode
    private EditText mGroupNameEditText;
    private List<ParseUser> mGroupMembers = new LinkedList<>();
    private List<String> mHints = new ArrayList<>();
    private ArrayAdapter<String> mHintsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMode = getIntent().getIntExtra("mode", ONE_TO_ONE);
        if (mMode == ONE_TO_ONE) {
            setContentView(R.layout.activity_new_11_chat);
            ListView mListView = (ListView) findViewById(R.id.users_list);
            mListView.setAdapter(mAllUsersAdapter);
        }
        else {
            setContentView(R.layout.activity_new_1n_chat);
            ListView mListView = (ListView) findViewById(R.id.group_list);
            mListView.setAdapter(mGroupMembersAdapter);
            mGroupNameEditText = (EditText) findViewById(R.id.group_name);
            mHintsAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, mHints);
            mHintsAdapter.setNotifyOnChange(true);

            final AutoCompleteTextView mNewMember = (AutoCompleteTextView) findViewById(R.id.new_member_actv);
            mNewMember.setAdapter(mHintsAdapter);

            mNewMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String s = (String) parent.getItemAtPosition(position);
                    ParseUser user = null;
                    for (ParseUser pu : mAllUsers){
                        if (pu.getUsername().equals(s)){
                            user = pu;
                            break;
                        }
                    }
                    if (user == null) return;
                    mGroupMembers.add(user);
                    mGroupMembersAdapter.notifyDataSetChanged();
                    mHintsAdapter.remove(user.getUsername());
                    mNewMember.setText("");
                }
            });

        }
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

        PoliLifeDB.getAllObjects(ParseUser.class, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMode == ONE_TO_MANY){
            getMenuInflater().inflate(R.menu.menu_new_1n_chat, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_group_chat:
                if (mGroupNameEditText.getText().length() == 0){
                    Toast.makeText(this, R.string.group_name_empty, Toast.LENGTH_SHORT).show();
                    break;
                }
                ArrayList<String> UUIDs = new ArrayList<>(mGroupMembers.size());
                for (int i=0;i<mGroupMembers.size();i++) {
                    UUIDs.add(mGroupMembers.get(i).getUsername());
                }
                Intent backIntent = new Intent();
                backIntent.putExtra("mode", mMode);
                backIntent.putStringArrayListExtra("params", UUIDs);
                setResult(Activity.RESULT_OK, backIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFetchSuccess(List<ParseUser> result) {
        mAllUsers = result;
        if (mMode == ONE_TO_ONE) {
            mAllUsersAdapter.notifyDataSetChanged();
        }
        else{
            mGroupMembers.clear();
            mHints.clear();
            for (ParseUser user : result) {
                mHints.add(user.getUsername());
            }
            mHintsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFetchError(Exception exception) {

    }

    private BaseAdapter mAllUsersAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mAllUsers.size();
        }
        @Override
        public ParseUser getItem(int position) {
            return mAllUsers.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(CreateChatActivity.this).inflate(
                        R.layout.layout_chat_friends_item, parent, false);
            }
            final ParseUser item = getItem(position);
            ((TextView) convertView.findViewById(R.id.friend_name)).setText(item.getUsername());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backIntent = new Intent();
                    backIntent.putExtra("mode", mMode);
                    backIntent.putExtra("params", item.getUsername());
                    setResult(Activity.RESULT_OK, backIntent);
                    finish();
                }
            });
            return convertView;
        }
    };

    private BaseAdapter mGroupMembersAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mGroupMembers.size();
        }
        @Override
        public ParseUser getItem(int position) {
            return mGroupMembers.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(CreateChatActivity.this).inflate(
                        R.layout.layout_chat_cancelable_friends_item, parent, false);
            }
            final ParseUser item = getItem(position);
            ((TextView) convertView.findViewById(R.id.friend_name)).setText(item.getUsername());
            convertView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGroupMembers.remove(item);
                    notifyDataSetChanged();
                    mHintsAdapter.add(item.getUsername());
                }
            });
            return convertView;
        }
    };
}
