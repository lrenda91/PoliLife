package it.polito.mad.polilife.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.polilife.R;

public class MessagingActivity extends AppCompatActivity {

    private String mChannel;
    private ListView mMessagesListView;
    private EditText mMsgEditText;
    private PubnubChatManager mChatManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        String UUID = getIntent().getStringExtra("UUID");
        mChannel = getIntent().getStringExtra("CHANNEL");
        mChatManager = new PubnubChatManager(UUID);
        List<String> l = new ArrayList<>();
        l.add(mChannel);
        mChatManager.init(l);

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
            public void onTextMessageSent(String channel, ChatMessage message) {
                mMsgEditText.setText("");
                adapter.addMessage(message);
                mMessagesListView.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onSubscribedToChannel(String channel) {

            }

            @Override
            public void onHistory(String channel, List<ChatMessage> messages) {
                adapter.setData(messages);
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsgEditText.getText().toString();
                mChatManager.sendMessage(mChannel, msg);
            }
        });
        mChatManager.history(mChannel, 30);
    }

    /*
    public void publish(String type, JSONObject data){
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        this.mPubNub.publish(this.mPrivateChannel, json, new Callback() {
            @Override
            public void successCallback(String s, Object o) {
                super.successCallback(s, o);
            }

            @Override
            public void connectCallback(String s, Object o) {
                super.connectCallback(s, o);
            }
        });
    }

    public void subscribeWithPresence(){
        Callback subscribeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                if (message instanceof JSONObject){
                    try {
                        JSONObject jsonObj = (JSONObject) message;
                        JSONObject json = jsonObj.getJSONObject("data");
                        String name = json.getString("chatUser");
                        String msg  = json.getString("chatMsg");
                        long time   = json.getLong("chatTime");
                        if (name.equals(mPubNub.getUUID())) return; // Ignore own messages
                        final ChatMessage chatMsg = new ChatMessage(name, msg, time);
                        MessagingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MessagingActivity.this, "sub success:"+chatMsg.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (JSONException e){ e.printStackTrace(); }
                }
                Log.d("PUBNUB", "Channel: " + channel + " Msg: " + message.toString());
            }

            @Override
            public void connectCallback(String channel, Object message) {
                Log.d("Subscribe", "Connected! " + message.toString());
                hereNow(false);
            }
        };
        try {
            List<String> channels = stud.getChannels();
            String[] ss = new String[channels.size()];
            for (int i=0;i<ss.length;i++) ss[i] = channels.get(i);
            mPubNub.subscribe(ss, subscribeCallback);
            Callback callback = new Callback() {
                @Override
                public void successCallback(String channel, Object response) {
                    Log.i("PN-pres","Pres: " + response.toString() + " class: " + response.getClass().toString());
                    if (response instanceof JSONObject){
                        final JSONObject json = (JSONObject) response;
                        Log.d("PN-main","Presence: " + json.toString());
                        try {
                            final int occ = json.getInt("occupancy");
                            final String user = json.getString("uuid");
                            final String action = json.getString("action");
                            MessagingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MessagingActivity.this, "Presence success:\n"+json.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (JSONException e){ e.printStackTrace(); }
                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("Presence", "Error: " + error.toString());
                }
            };
            try {
                this.mPubNub.presence(this.mPrivateChannel, callback);
            } catch (PubnubException e) { e.printStackTrace(); }
        } catch (Exception e){ e.printStackTrace(); }
    }

    private static boolean isValidMessage(String msg){
        return true;
    }

    public void hereNow(final boolean displayUsers) {
        this.mPubNub.hereNow(this.mPrivateChannel, new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                try {
                    JSONObject json = (JSONObject) response;
                    final int occ = json.getInt("occupancy");
                    final JSONArray hereNowJSON = json.getJSONArray("uuids");
                    Log.d("JSON_RESP", "Here Now: " + json.toString());
                    final Set<String> usersOnline = new HashSet<String>();
                    //usersOnline.add(username);
                    for (int i = 0; i < hereNowJSON.length(); i++) {
                        usersOnline.add(hereNowJSON.getString(i));
                    }
                    MessagingActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MessagingActivity.this, "Here now:\n"+ usersOnline.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void history(){
        this.mPubNub.history(ParseUser.getCurrentUser().getObjectId(), 100, false, new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);
                    final List<ChatMessage> chatMsgs = new ArrayList<ChatMessage>();
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String name = jsonMsg.getString("chatUser");
                            String msg = jsonMsg.getString("chatMsg");
                            long time = jsonMsg.getLong("chatTime");
                            ChatMessage chatMsg = new ChatMessage(name, msg, time);
                            chatMsgs.add(chatMsg);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }
                    MessagingActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MessagingActivity.this, "history:\n"+ chatMsgs.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("History", error.toString());
            }
        });
    }
    */






}
