package it.polito.mad.polilife.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.polilife.R;

public class MessagingActivity extends AppCompatActivity {

    private String mChannel;
    private ListView mMessagesListView;
    private EditText mMsgEditText;
    private PubnubChatManager mChatManager = PubnubChatManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
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

        mChannel = getIntent().getStringExtra("CHANNEL");
        mChatManager.init(mChannel);
        getSupportActionBar().setTitle(mChannel);

        mMsgEditText = (EditText) findViewById(R.id.msg);
        mMessagesListView = (ListView) findViewById(R.id.messages_list);
        final ChatMessagesBaseAdapter adapter = new ChatMessagesBaseAdapter(this, mChatManager.getUUID());
        mMessagesListView.setAdapter(adapter);
        mChatManager.setChatListener(new PubnubChatManager.ChatListener() {
            @Override
            public void onTextMessageReceived(String channel, ChatMessage message) {
                adapter.addMessage(message);
            }

            @Override
            void onTextMessageSent(String channel, ChatMessage message) {
                adapter.addMessage(message);
                mMsgEditText.setText("");
            }

            @Override
            public void onHistory(String channel, List<ChatMessage> messages) {
                adapter.setData(messages);
            }

            @Override
            void onJoin(String channel, String UUID, int occupancy) {
                Toast.makeText(MessagingActivity.this, UUID+" joined", Toast.LENGTH_SHORT).show();
            }

            @Override
            void onLeave(String channel, String UUID, int occupancy) {
                Toast.makeText(MessagingActivity.this, UUID+" leaved", Toast.LENGTH_SHORT).show();
            }

        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsgEditText.getText().toString();
                mChatManager.sendMessage(mChannel, msg);
            }
        });
        mChatManager.history(mChannel, 50);
    }

    @Override
    protected void onPause() {
        mChatManager.unsubscribe(mChannel);
        super.onPause();
    }
}
