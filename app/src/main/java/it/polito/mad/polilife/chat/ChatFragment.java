package it.polito.mad.polilife.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Student;

/**
 * Created by luigi onSelectAppliedJobs 01/01/16.
 */
public class ChatFragment extends Fragment {

    public static ChatFragment newInstance(){
        return new ChatFragment();
    }

    private Student mUser = (Student) ParseUser.getCurrentUser();

    private PubnubChatManager mChatManager = PubnubChatManager.getInstance();

    private List<String> mSubscriptions;
    private int mSelected = 0;

    private BaseAdapter mListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mSubscriptions.size();
        }

        @Override
        public String getItem(int position) {
            String sub = mSubscriptions.get(position);
            if (!sub.contains("-")) return sub;
            StringTokenizer st = new StringTokenizer(sub, "-");
            String t1 = st.nextToken();
            String t2 = st.nextToken();
            return t1.equals(mUser.getUsername()) ? t2 : t1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_chat_friends_item, parent, false);
            }
            final String item = getItem(position);
            ((TextView) convertView.findViewById(R.id.friend_name)).setText(item);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MessagingActivity.class);
                    intent.putExtra("UUID", mUser.getUsername());
                    intent.putExtra("CHANNEL", mSubscriptions.get(position));
                    startActivity(intent);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Cancel chat");
                    DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    List<String> l = new ArrayList<String>();
                                    l.add(item);
                                    mSubscriptions.remove(item);
                                    mUser.removeChannel(l);
                                    mUser.saveEventually();
                                    notifyDataSetChanged();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    builder.setCancelable(false)
                            .setPositiveButton("OK", onClick)
                            .setNegativeButton("Cancel", onClick);
                    final AlertDialog alert = builder.create();
                    alert.show();
                    return false;
                }
            });
            return convertView;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatManager.setChatListener(new PubnubChatManager.ChatListener() {
            @Override
            void onJoinRequestReceived(String channel) {
                mUser.addChannel(channel);
                mUser.saveEventually();
                if (!mSubscriptions.contains(channel)) {
                    mSubscriptions.add(channel);
                    mListAdapter.notifyDataSetChanged();
                }
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
        mSubscriptions = mUser.getChannels() != null ? mUser.getChannels() : new ArrayList<String>();
        mChatManager.init(mSubscriptions);

        ((ListView) view.findViewById(R.id.conversations_list)).setAdapter(mListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent fromActivity = getActivity().getIntent();
        if (fromActivity.hasExtra("json")){
            mChatManager.handleJSON(fromActivity.getStringExtra("json"));
        }
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
