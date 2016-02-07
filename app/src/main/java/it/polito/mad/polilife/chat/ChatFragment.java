package it.polito.mad.polilife.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import org.json.JSONObject;

import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import it.polito.mad.polilife.PushListener;
import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Student;

/**
 * Created by luigi onSelectAppliedJobs 01/01/16.
 */
public class ChatFragment extends Fragment implements PushListener {

    public static ChatFragment newInstance(){
        return new ChatFragment();
    }

    private Student mUser = (Student) ParseUser.getCurrentUser();

    private PubnubChatManager mChatManager = PubnubChatManager.getInstance();

    private ChannelsAdapter mListAdapter;
    private int mSelected = 0;

    @Override
    public void onPushReceived(JSONObject message) {
        mChatManager.handleJSON(message);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatManager.setChatListener(new PubnubChatManager.ChatListener() {
            @Override
            void onJoinRequestReceived(String channel) {
                mUser.addChannel(channel);
                mUser.saveEventually();
                mListAdapter.addToData(channel);
                Toast.makeText(getActivity(), "Join request for channel "+channel, Toast.LENGTH_LONG).show();
            }
            @Override
            void onJoin(String channel, String UUID, int occupancy) {
                Toast.makeText(getActivity(), UUID+" joined channel "+channel, Toast.LENGTH_LONG).show();
            }
            @Override
            void onLeave(String channel, String UUID, int occupancy) {
                Toast.makeText(getActivity(), UUID+" left channel "+channel, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Show dialog");
                final CharSequence[] choiceList = {"One to one", "Group"};
                DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent i = new Intent(getActivity(), CreateChatActivity.class);
                                i.putExtra("mode",mSelected);
                                startActivityForResult(i, CreateChatActivity.NEW_CHAT_REQUEST_CODE);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                            default:
                                mSelected = which;
                                break;
                        }
                    }
                };
                builder.setSingleChoiceItems(choiceList, mSelected, onClick)
                        .setCancelable(false)
                        .setPositiveButton("OK", onClick)
                        .setNegativeButton("Cancel", onClick);
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });
        List<String> mSubscriptions = mUser.getChannels() != null ?
                mUser.getChannels() : new ArrayList<String>();
        mListAdapter = new ChannelsAdapter(getActivity());
        mListAdapter.setData(mSubscriptions);
        ((ListView) view.findViewById(R.id.conversations_list)).setAdapter(mListAdapter);
        mChatManager.init(mListAdapter.getData());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CreateChatActivity.NEW_CHAT_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                switch(data.getIntExtra("mode", -1)){
                    case CreateChatActivity.ONE_TO_ONE:
                        String otherUUID = data.getStringExtra("params");
                        mChatManager.newOneToOneChat(otherUUID);
                        break;
                    case CreateChatActivity.ONE_TO_MANY:
                        String groupName = data.getStringExtra("groupName");
                        List<String> UUIDs = data.getStringArrayListExtra("params");
                        mChatManager.newGroupChat(groupName, UUIDs);
                        break;
                }
            }
        }
     }
}
