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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import it.polito.mad.polilife.db.classes.Student;
import it.polito.mad.polilife.db.push.JSONFactory;
import it.polito.mad.polilife.db.push.PushManager;


/**
 * Created by luigi onSelectAppliedJobs 04/01/16.
 */
public class PubnubChatManager {

    private static final String TAG = "PUBNUB";
    private static final String PUBLISH_KEY = "pub-c-a43bf649-597f-447e-98b5-c87b5cb1568b";
    private static final String SUBSCRIBE_KEY = "sub-c-cd6a58d4-ad3d-11e5-ae71-02ee2ddab7fe";

    private static final String PUBLIC_CHANNEL = "public";

    private static PubnubChatManager mInstance;
    public static PubnubChatManager getInstance(){
        if (mInstance == null){
            String UUID = ParseUser.getCurrentUser().getUsername();
            mInstance = new PubnubChatManager(UUID);
        }
        return mInstance;
    }

    private static final Student sUser = (Student) ParseUser.getCurrentUser();

    private Pubnub mPubnub;
    private ChatListener mListener;
    private Handler mMainHandler;

    private PubnubChatManager(String UUID){
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

        public void handleJSON(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            mSubscribeCallback.successCallback(PUBLIC_CHANNEL, jsonObject);
        }catch(JSONException e){}
    }

    public void init(String... channels){
        List<String> list = new LinkedList<>();
        for (String s : channels) list.add(s);
        list.add(PUBLIC_CHANNEL);
        init(list);
    }

    public void init(List<String> channels){
        try {
            String[] ss = new String[channels.size()+1];
            for (int i=0;i<channels.size();i++) ss[i] = channels.get(i);
            ss[ss.length-1] = PUBLIC_CHANNEL;
            mPubnub.subscribe(ss, mSubscribeCallback);
            for (String s : channels){
                mPubnub.presence(s, mPresenceCallback);
            }
        } catch (Exception e){ e.printStackTrace(); }
    }

    public void unsubscribe(String channel){
        try{
            //String[] ss = { channel, PUBLIC_CHANNEL };
            mPubnub.unsubscribe(channel);
        }
        catch(Exception e){ e.printStackTrace(); }
        Log.d(TAG, "Unsubcribed from channel "+channel);
    }

    public void newOneToOneChat(String receiverUUID){
        if (mPubnub.getUUID().equals(receiverUUID)){
            return;
        }
        try {
            String pairChannel = getChannelName(mPubnub.getUUID(), receiverUUID);
            List<String> receivers = Arrays.asList(mPubnub.getUUID(), receiverUUID);
            JSONObject request = JSONFactory.createChatSetupMessage(pairChannel, receivers);
            PushManager.sendPushNotification(Arrays.asList(PUBLIC_CHANNEL), request, null);
        }
        catch(Exception e){
            e.printStackTrace();
            if (mListener != null) mListener.onError(e);
        }
    }

    public void newGroupChat(String groupName, List<String> receiversUUIDs){
        receiversUUIDs.add(mPubnub.getUUID());
        try {
            JSONObject request = JSONFactory.createChatSetupMessage(groupName, receiversUUIDs);
            PushManager.sendPushNotification(Arrays.asList(PUBLIC_CHANNEL), request, null);
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
            if (message instanceof JSONObject){
                try {
                    JSONObject jsonObject = (JSONObject) message;
                    if (channel.equals(PUBLIC_CHANNEL)){
                        if (jsonObject.has("type") && jsonObject.get("type").equals("setup")){
                            //String sender = jsonObject.getString("senderID");
                            JSONArray receivers = jsonObject.getJSONArray("receiverIDs");
                            for (int i=0; i<receivers.length(); i++){
                                String id = receivers.getString(i);
                                if (id.equals(mPubnub.getUUID())){  //I'm one of receivers
                                    final String channelToSetup = jsonObject.getString("group");
                                    JSONObject state = new JSONObject();
                                    state.put("first", sUser.getFirstName());
                                    state.put("last", sUser.getLastName());
                                    mPubnub.setState(channelToSetup, id, state, new Callback() {});
                                    mPubnub.subscribe(channelToSetup, this);
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mListener != null)
                                                mListener.onJoinRequestReceived(channelToSetup);
                                        }
                                    });
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
                                if (mListener != null){
                                    if (msg.senderUsername.equals(mPubnub.getUUID()))
                                        mListener.onTextMessageSent(channel, msg);
                                    else
                                        mListener.onTextMessageReceived(channel, msg);
                                }

                            }
                        });
                    }








                    /*if (jsonObject.has("type")) {
                        if (jsonObject.getString("type").equals("setup_1_to_1")) {
                            String receiver = jsonObject.getString("receiverIDs");
                            String sender = jsonObject.getString("senderID");
                            if (!receiver.equals(mPubnub.getUUID()) &&
                                    !sender.equals(mPubnub.getUUID()) ){
                                return;
                            }
                            final String newChannel = getChannelName(sender, receiver);
                            //This message will be received ALSO by myself
                            JSONObject state = new JSONObject();
                            state.put("first", sUser.getFirstName());
                            state.put("last", sUser.getLastName());
                            mPubnub.setState(newChannel, mPubnub.getUUID(), state, new Callback() {});

                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mListener != null)
                                        mListener.onJoinRequestReceived(newChannel);
                                }
                            });
                        }
                        else if (jsonObject.getString("type").equals("setup_1_to_N")) {
                            JSONArray uuids = jsonObject.getJSONArray("receiverIDs");
                            for (int i=0; i<uuids.length(); i++){
                                String id = uuids.getString(i);
                                if (id.equals(mPubnub.getUUID())){  //I'm one of receivers
                                    final String groupChannelName = jsonObject.getString("group");
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mListener != null)
                                                mListener.onJoinRequestReceived(groupChannelName);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }
                    */

                }
                catch(JSONException e){
                    e.printStackTrace();
                    Log.e(TAG, "Error onSelectAppliedJobs jsons in subscribe, "+e.getClass().getSimpleName()+" : "+e.getMessage());
                }
                catch(PubnubException e){
                    e.printStackTrace();
                    Log.e(TAG, "Error onSelectAppliedJobs jsons in subscribe, "+e.getClass().getSimpleName()+" : "+e.getMessage());
                }
            }
        }
        @Override
        public void connectCallback(final String channel, Object message) {
            Log.d(TAG, "Subscription onSelectAppliedJobs channel '" + channel + "' OK! " + message.toString());
            if (!channel.equals(PUBLIC_CHANNEL)) {
                hereNow(channel);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onSubscribed(channel);
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
        public void successCallback(final String channel, Object response) {
            //Log.i(TAG, "Presence success: " + response.toString() + " class: " + response.getClass().toString());
            if (response instanceof JSONObject){
                final JSONObject json = (JSONObject) response;
                try {
                    final int occ = json.getInt("occupancy");
                    final String user = json.getString("uuid");
                    final String action = json.getString("action");
                    if (action.equals("join")){
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) mListener.onJoin(channel, user, occ);
                            }
                        });
                    }
                    else if (action.equals("leave")){
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) mListener.onLeave(channel, user, occ);
                            }
                        });
                    }
                    Log.i(TAG, json.toString());
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
                JSONObject json = (JSONObject) response;
                final int occ = json.getInt("occupancy");
                final JSONArray hereNowJSON = json.getJSONArray("uuids");
                final Set<String> usersOnline = new HashSet<String>();
                //usersOnline.add(senderUsername);
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
        if (UUID1.compareTo(UUID2) <= 0) return UUID1 + "-" + UUID2;
        return UUID2 + "-" + UUID1;
    }

    public static abstract class ChatListener {
        void onTextMessageReceived(String channel, ChatMessage message){}
        void onJoinRequestReceived(String channel){}
        void onTextMessageSent(String channel, ChatMessage message){}
        void onSubscribed(String channel){}
        void onHistory(String channel, List<ChatMessage> messages){}
        void onJoin(String channel, String UUID, int occupancy){}
        void onLeave(String channel, String UUID, int occupancy){}
        void onError(Exception e){}
    }

}
