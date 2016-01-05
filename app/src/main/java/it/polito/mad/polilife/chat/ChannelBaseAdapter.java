package it.polito.mad.polilife.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.ChatChannel;
import it.polito.mad.polilife.db.classes.ChatChannel;

/**
 * Created by luigi on 01/01/16.
 */
public class ChannelBaseAdapter extends BaseAdapter {

    interface onChatClickListener {
        void onClick(View itemView, int position, ChatChannel item);
    }

    private Context mContext;
    private List<ChatChannel> mData;
    private onChatClickListener mListener;

    public ChannelBaseAdapter(Context context){
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<ChatChannel> data){
        mData = data;
    }

    public void setOnChatClickListener(onChatClickListener listener){
        mListener = listener;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ChatChannel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_chat_friends_item, parent, false);
        }
        final ChatChannel item = getItem(position);
        ((TextView)convertView.findViewById(R.id.friend_name)).setText("caiao");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(v, position, item);
                }
            }
        });
        return convertView;
    }

}
