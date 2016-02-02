package it.polito.mad.polilife.chat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Student;

public class MessagingActivity extends AppCompatActivity
    implements DBCallbacks.GetListCallback<Student> {

    private String mChannel;
    private ListView mMessagesListView;
    private EditText mMsgEditText;
    private TextView mLastUserTV, mLastAccessTV;
    private ImageView mLastAccessPhoto;
    private PubnubChatManager mChatManager = PubnubChatManager.getInstance();

    private String mLastAccess;
    private String mDateFormat;
    private HashMap<String, Student> mMembersStateMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mDateFormat = getString(R.string.datetime_format);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        mLastUserTV = (TextView) findViewById(R.id.last_access_name);
        mLastAccessTV = (TextView) findViewById(R.id.last_access_time);
        mLastAccessPhoto = (ImageView) findViewById(R.id.last_access_photo);
        mMsgEditText = (EditText) findViewById(R.id.msg);
        mMessagesListView = (ListView) findViewById(R.id.messages_list);
        final ChatMessagesBaseAdapter adapter = new ChatMessagesBaseAdapter(this, mChatManager.getUUID());
        mMessagesListView.setAdapter(adapter);

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsgEditText.getText().toString();
                mChatManager.sendMessage(mChannel, msg);
            }
        });

        mChannel = getIntent().getStringExtra("CHANNEL");
        /*if (!mChannel.contains("-")){
            getSupportActionBar().setTitle(mChannel);
        }
        else{
            StringTokenizer st = new StringTokenizer(mChannel, "-");
            String t1 = st.nextToken();
            String t2 = st.nextToken();
            getSupportActionBar().setTitle(
                    t1.equals(ParseUser.getCurrentUser().getUsername()) ? t2 : t1 );
        }
           */


        mChatManager.setChatListener(new PubnubChatManager.ChatListener() {
            @Override
            public void onTextMessageReceived(String channel, ChatMessage message) {
                Student s = mMembersStateMap.get(message.senderUsername);
                message.senderCompleteName = s.getFirstName() + " " + s.getLastName();
                if (s.getPhoto() != null){
                    try {
                        message.photoBytes = s.getPhoto().getData();
                    }catch(ParseException e){ }
                }
                adapter.addMessage(message);
                updateLastMsg(message);
            }

            @Override
            void onTextMessageSent(String channel, ChatMessage message) {
                Student s = mMembersStateMap.get(message.senderUsername);
                message.senderCompleteName = s.getFirstName() + " " + s.getLastName();
                if (s.getPhoto() != null){
                    try {
                        message.photoBytes = s.getPhoto().getData();
                    }catch(ParseException e){ }
                }
                adapter.addMessage(message);
                mMsgEditText.setText("");
                updateLastMsg(message);
            }

            @Override
            public void onHistory(String channel, List<ChatMessage> messages) {
                for (ChatMessage msg : messages){
                    Student s = mMembersStateMap.get(msg.senderUsername);
                    if (s == null) continue;
                    msg.senderCompleteName = s.getFirstName() + " " + s.getLastName();
                    if (s.getPhoto() != null){
                        try {
                            msg.photoBytes = s.getPhoto().getData();
                        }catch(ParseException e){ }
                    }
                }
                adapter.setData(messages);
                updateLastMsg(messages.get(messages.size()-1));
            }

            @Override
            void onJoin(String channel, String UUID, int occupancy) {
            }

            @Override
            void onLeave(String channel, String UUID, int occupancy) {
            }

        });

        PoliLifeDB.getChatMembers(mChannel, this);

    }

    private void updateLastMsg(ChatMessage msg){
        if (mLastAccess == null || !mLastAccess.equals(msg.senderUsername)){
            mLastAccess = msg.senderUsername;
            if (msg.photoBytes != null) {
                mLastAccessPhoto.setImageBitmap(
                        BitmapFactory.decodeByteArray(msg.photoBytes, 0, msg.photoBytes.length)
                );
            }
        }
        mLastUserTV.setText(msg.senderCompleteName);
        mLastAccessTV.setText(new SimpleDateFormat(mDateFormat).format(new Date(msg.timeStamp)).toString());
    }

    @Override
    public void onFetchSuccess(List<Student> result) {
        for (Student student : result){
            mMembersStateMap.put(student.getUsername(), student);
        }
        mChatManager.init(mChannel);
        mChatManager.history(mChannel, 100);
    }

    @Override
    public void onFetchError(Exception exception) {
        Toast.makeText(this, "Problems in fetching group members", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        mChatManager.unsubscribe(mChannel);
        super.onPause();
    }
}
