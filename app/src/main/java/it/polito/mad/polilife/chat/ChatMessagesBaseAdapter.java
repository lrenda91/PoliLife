package it.polito.mad.polilife.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.polilife.R;

/**
 * Created by luigi on 26/12/15.
 */
public class ChatMessagesBaseAdapter extends BaseAdapter {

    interface onChatMessageClickListener {
        void onClick(View itemView, int position);
    }

    private Context mContext;
    private List<ChatMessage> mData;
    private onChatMessageClickListener mListener;
    private String mCurrentUsername;

    public ChatMessagesBaseAdapter(Context context, String myID){
        mContext = context;
        mCurrentUsername = myID;
        mData = new ArrayList<>();
    }

    public void addMessage(ChatMessage msg){
        mData.add(msg);
        notifyDataSetChanged();
    }

    public void setData(List<ChatMessage> data){
        mData = data;
        notifyDataSetChanged();
    }

    public void setOnChatMessageClickListener(onChatMessageClickListener listener){
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChatMessage msg = getItem(position);
        if (convertView == null) {
            int layoutID = msg.username.equals(mCurrentUsername) ?
                    R.layout.layout_message_right :
                    R.layout.layout_message_left;
            convertView = LayoutInflater.from(mContext).inflate(layoutID, parent, false);
            Log.d("PUBNUB", "Dest: "+msg.username+" , layout="+
                    ((layoutID==R.layout.layout_message_right) ? "right" : "left"));
        }
        ((TextView)convertView.findViewById(R.id.msg_text)).setText(msg.message);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(v, position);
                }
            }
        });
        return convertView;
    }
}
