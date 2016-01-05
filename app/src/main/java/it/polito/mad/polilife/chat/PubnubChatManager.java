package it.polito.mad.polilife.chat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import it.polito.mad.polilife.db.classes.Student;

/**
 * Created by luigi on 04/01/16.
 */
public class PubnubChatManager {

    private static final String TAG = "PUBNUB";
    private static final String PUBLISH_KEY = "pub-c-a43bf649-597f-447e-98b5-c87b5cb1568b";
    private static final String SUBSCRIBE_KEY = "sub-c-cd6a58d4-ad3d-11e5-ae71-02ee2ddab7fe";

    private static final String PUBLIC_CHANNEL = "public";

    private Student mUser;
    private Pubnub mPubnub;
    private ChatListener mListener;
    private Handler mMainhandler;

    public PubnubChatManager(){
        mMainhandler = new Handler(Looper.getMainLooper());
        mUser = (Student) ParseUser.getCurrentUser();
        mPubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        mPubnub.setUUID(mUser.getUsername());
        init();
    }

    public String getUUID(){
        return mPubnub.getUUID();
    }

    public void setChatListener(ChatListener listener) {
        mListener = listener;
    }

    public void init(){
        try {
            List<String> channels = mUser.getChannels() != null ?
                    mUser.getChannels() : new ArrayList<String>();
            channels.add(PUBLIC_CHANNEL);
            String[] ss = new String[channels.size()];
            for (int i=0;i<ss.length;i++) ss[i] = channels.get(i);
            mPubnub.subscribe(ss, mSubscribeCallback);
            //mPubnub.presence(this.mPrivateChannel, mPresenceCallback);
        } catch (Exception e){ e.printStackTrace(); }
    }

    public void newOneToOneChat(String receiverUUID){
        if (mPubnub.getUUID().equals(receiverUUID)){
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "setup_1_to_1");
            obj.put("senderID", mPubnub.getUUID());
            obj.put("receiverID", receiverUUID);
            String newPairChannelName = getChannelName(mPubnub.getUUID(), receiverUUID);
            mPubnub.publish(PUBLIC_CHANNEL, obj, mPublishCallback);
            mPubnub.subscribe(newPairChannelName, mSubscribeCallback);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void newGroupChat(String groupName, List<String> receiversUUID){
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "setup_1_to_N");
            obj.put("senderID", mPubnub.getUUID());
            obj.put("group", groupName);
            obj.put("receiverID", new JSONArray(receiversUUID));
            mPubnub.publish(PUBLIC_CHANNEL, obj, mPublishCallback);
            mPubnub.subscribe(groupName, mSubscribeCallback);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void hereNow(String channel) {
        mPubnub.hereNow(channel, mHereNowCallback);
    }

    public void history(String channel, int count){
        mPubnub.history(channel, count, false, mHistoryCallback);
    }

    public void sendMessage(String channel, String content){
        mPubnub.publish(channel, content, mPublishCallback);
    }


    private Callback mSubscribeCallback = new Callback() {
        @Override
        public void successCallback(final String channel, Object message) {
            Log.d(TAG, "Received on channel: " + channel + " Msg: " + message.toString());
            if (message instanceof JSONObject){
                try {
                    JSONObject jsonObject = (JSONObject) message;
                    if (jsonObject.has("type")) {
                        if (jsonObject.getString("type").equals("setup_1_to_1")) {
                            String receiver = jsonObject.getString("receiverID");
                            if (!receiver.equals(mPubnub.getUUID())){
                                Log.d(TAG, "Discard!! I'm not the receiver");
                                return;
                            }
                            String sender = jsonObject.getString("senderID");
                            String newChannel = getChannelName(sender, receiver);
                            mPubnub.subscribe(newChannel, this);
                        }
                        else if (jsonObject.getString("type").equals("setup_1_to_N")) {
                            JSONArray uuids = jsonObject.getJSONArray("receiverID");
                            for (int i=0; i<uuids.length(); i++){
                                String id = uuids.getString(i);
                                if (id.equals(mPubnub.getUUID())){  //I'm one of receivers
                                    String groupChannelName = jsonObject.getString("group");
                                    mPubnub.subscribe(groupChannelName, this);
                                    break;
                                }
                            }
                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "Error in subscribe, "+e.getClass().getSimpleName()+" : "+e.getMessage());
                }
            }
            else if (message instanceof String){
                final ChatMessage msg = new ChatMessage(mPubnub.getUUID(), (String)message, System.currentTimeMillis());
                mMainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onTextMessageReceived(channel, msg);
                    }
                });
            }
        }
        @Override
        public void connectCallback(final String channel, Object message) {
            Log.d(TAG, "Subscription on channel '" + channel + "' OK! " + message.toString());
            mMainhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) mListener.onSubscribedToChannel(channel);
                }
            });
            if (!channel.equals(PUBLIC_CHANNEL)) {
                mUser.addChannel(channel);
                mUser.saveInBackground();
                hereNow(channel);
            }
        }
        @Override
        public void errorCallback(String s, PubnubError pubnubError) {
            Log.e(TAG, "Error in subscribe(): " + pubnubError.toString());
        }
    };

    private Callback mPublishCallback = new Callback() {
        @Override
        public void successCallback(final String s, Object o) {
            Log.d(TAG, "Publish success: " + o);
            final ChatMessage msg = new ChatMessage(mPubnub.getUUID(), o.toString(), System.currentTimeMillis());
            mMainhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) mListener.onTextMessageSent(s, msg);
                }
            });
        }

        @Override
        public void errorCallback(String s, PubnubError pubnubError) {
            Log.d(TAG, "Publish error: " + pubnubError.toString());
        }
    };

    private Callback mHistoryCallback = new Callback() {
        @Override
        public void successCallback(final String channel, final Object message) {
            Log.d(TAG, "History success: " + message.toString());
            try {
                JSONArray json = (JSONArray) message;
                final JSONArray messages = json.getJSONArray(0);
                final List<ChatMessage> chatMsgs = new LinkedList<>();
                for (int i = 0; i < messages.length(); i++) {
                    try {/*
                        if (!messages.getJSONObject(i).has("data")) continue;
                        JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                        String name = jsonMsg.getString("chatUser");
                        String msg = jsonMsg.getString("chatMsg");
                        long time = jsonMsg.getLong("chatTime");*/
                        String msg = messages.getString(i);
                        ChatMessage chatMsg = new ChatMessage(mPubnub.getUUID(), msg, System.currentTimeMillis());
                        chatMsgs.add(chatMsg);
                    } catch (JSONException e) { // Handle errors silently
                        e.printStackTrace();
                    }
                }
                mMainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onHistory(channel, chatMsgs);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.d(TAG, "History error:" + error.toString());
        }
    };


    private Callback mPresenceCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object response) {
            Log.i(TAG, "Presence success: " + response.toString() + " class: " + response.getClass().toString());
            if (response instanceof JSONObject){
                final JSONObject json = (JSONObject) response;
                try {
                    final int occ = json.getInt("occupancy");
                    final String user = json.getString("uuid");
                    final String action = json.getString("action");
                } catch (JSONException e){ e.printStackTrace(); }
            }
        }
        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.d(TAG, "Presence error: " + error.toString());
        }
    };

    private Callback mHereNowCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object response) {
            try {
                Log.d(TAG, "HereNow on channel '"+channel+"': "+response.toString());
                System.out.println(response.toString());
                JSONObject json = (JSONObject) response;
                final int occ = json.getInt("occupancy");
                final JSONArray hereNowJSON = json.getJSONArray("uuids");
                final Set<String> usersOnline = new HashSet<String>();
                //usersOnline.add(username);
                for (int i = 0; i < hereNowJSON.length(); i++) {
                    usersOnline.add(hereNowJSON.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void errorCallback(String s, PubnubError pubnubError) {
            Log.e(TAG, "HereNow error: "+pubnubError.toString());
        }
    };

    private static String getChannelName(String UUID1, String UUID2){
        return UUID1 + "-" + UUID2;
    }

    public interface ChatListener {
        void onTextMessageReceived(String channel, ChatMessage message);
        void onTextMessageSent(String channel, ChatMessage message);
        void onSubscribedToChannel(String channel);
        void onHistory(String channel, List<ChatMessage> messages);
    }

}
