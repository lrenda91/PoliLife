package it.polito.mad.polilife.chat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Created by luigi on 04/01/16.
 */
public class PubnubChatManager {

    private static final String TAG = "PUBNUB";
    private static final String PUBLISH_KEY = "pub-c-a43bf649-597f-447e-98b5-c87b5cb1568b";
    private static final String SUBSCRIBE_KEY = "sub-c-cd6a58d4-ad3d-11e5-ae71-02ee2ddab7fe";

    private static final String PUBLIC_CHANNEL = "public";

    private Pubnub mPubnub;
    private ChatListener mListener;
    private Handler mMainHandler;

    public PubnubChatManager(String UUID){
        mMainHandler = new Handler(Looper.getMainLooper());
        mPubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        mPubnub.setUUID(UUID);
    }

    public String getUUID(){
        return mPubnub.getUUID();
    }

    public void setChatListener(ChatListener listener) {
        mListener = listener;
    }

    public void init(List<String> channels){
        try {
            String[] ss = new String[channels.size()+1];
            for (int i=0;i<channels.size();i++) ss[i] = channels.get(i);
            ss[ss.length-1] = PUBLIC_CHANNEL;
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
            if (mListener != null) mListener.onError(e);
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
        ChatMessage msg = new ChatMessage(mPubnub.getUUID(), content, System.currentTimeMillis());
        try{
            mPubnub.publish(channel, msg.toJson(), mPublishCallback);
        }
        catch(JSONException e){
            Log.e(TAG, "Error in sendMessage(): "+e.getMessage());
            e.printStackTrace();
        }
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
                    else if (jsonObject.has("ts")){
                        final ChatMessage msg = ChatMessage.fromJSON(jsonObject);
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null)
                                    mListener.onTextMessageReceived(channel, msg);
                            }
                        });
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                    Log.e(TAG, "Error on jsons in subscribe, "+e.getClass().getSimpleName()+" : "+e.getMessage());
                }
                catch(PubnubException e){
                    e.printStackTrace();
                    Log.e(TAG, "Error on pubnub in subscribe, "+e.getClass().getSimpleName()+" : "+e.getMessage());
                }
            }
        }
        @Override
        public void connectCallback(final String channel, Object message) {
            Log.d(TAG, "Subscription on channel '" + channel + "' OK! " + message.toString());
            if (!channel.equals(PUBLIC_CHANNEL)) {
                hereNow(channel);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onSubscribedToChannel(channel);
                    }
                });
            }
        }
        @Override
        public void errorCallback(String s, PubnubError pubnubError) {
            Log.e(TAG, "Error in subscribe(): " + pubnubError.toString());
        }

        @Override
        public void reconnectCallback(String s, Object o) {
            super.reconnectCallback(s, o);
        }

        @Override
        public void disconnectCallback(String s, Object o) {
            super.disconnectCallback(s, o);
        }
    };

    private Callback mPublishCallback = new Callback() {
        @Override
        public void successCallback(final String s, Object o) {
            Log.d(TAG, "Publish success: " + o.getClass().getSimpleName() + "->" + o.toString());
            if (o instanceof JSONObject){
                JSONObject jsonObject = (JSONObject) o;
                try{
                    final ChatMessage msg = ChatMessage.fromJSON(jsonObject);
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) mListener.onTextMessageSent(s, msg);
                        }
                    });
                }
                catch(JSONException e){

                }
            }
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
            if (message instanceof JSONArray){
                JSONArray historyArray = (JSONArray) message;
                JSONArray messages = null;
                try {
                    messages = historyArray.getJSONArray(0);
                }
                catch(JSONException e){
                    return;
                }
                final List<ChatMessage> chatMsgs = new LinkedList<>();
                for (int i=0; i<messages.length(); i++){
                    try{
                        JSONObject jsonMessage = messages.getJSONObject(i);
                        chatMsgs.add(ChatMessage.fromJSON(jsonMessage));
                    }
                    catch(JSONException e){
                        Log.w(TAG, "History message #"+i+" is not a message json");
                        continue;
                    }
                }
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onHistory(channel, chatMsgs);
                    }
                });
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

    public static abstract class ChatListener {
        void onTextMessageReceived(String channel, ChatMessage message){}
        void onTextMessageSent(String channel, ChatMessage message){}
        void onSubscribedToChannel(String channel){}
        void onHistory(String channel, List<ChatMessage> messages){}
        void onError(Exception e){}
    }

}
