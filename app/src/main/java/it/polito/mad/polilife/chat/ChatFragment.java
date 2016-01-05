package it.polito.mad.polilife.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.ChatChannel;
import it.polito.mad.polilife.db.classes.Student;

/**
 * Created by luigi on 01/01/16.
 */
public class ChatFragment extends Fragment {

    public static ChatFragment newInstance(){
        return new ChatFragment();
    }

    private Student mUser = (Student) ParseUser.getCurrentUser();


    private ListView mConversations;
    private List<String> users;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = mUser.getChannels() != null ? mUser.getChannels() : new ArrayList<String>();
        mConversations = (ListView) view.findViewById(R.id.conversations_list);
        mConversations.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return users.size();
            }

            @Override
            public String getItem(int position) {
                return users.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
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
                        intent.putExtra("CHANNEL", item);
                        startActivity(intent);
                    }
                });
                return convertView;
            }
        });
    }


}
