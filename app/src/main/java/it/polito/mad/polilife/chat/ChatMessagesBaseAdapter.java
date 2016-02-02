package it.polito.mad.polilife.chat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;

/**
 * Created by luigi onSelectAppliedJobs 26/12/15.
 */
public class ChatMessagesBaseAdapter extends BaseAdapter {

    interface onChatMessageClickListener {
        void onClick(View itemView, int position);
    }

    private Context mContext;
    private List<ChatMessage> mData;
    private onChatMessageClickListener mListener;
    private String mMyUsername;
    private DateFormat mDateFormat;
    private int[] mColors;

    public ChatMessagesBaseAdapter(Context context, String myID){
        mContext = context;
        mMyUsername = myID;
        mData = new ArrayList<>();
        mDateFormat = new SimpleDateFormat(mContext.getString(R.string.datetime_format));
        mColors = mContext.getResources().getIntArray(R.array.androidcolors);
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
        boolean sent = msg.senderUsername.equals(mMyUsername);
        //boolean recycledConvertView = (convertView != null);
        //int gravity = sent ? Gravity.RIGHT : Gravity.LEFT;
        //Log.d("adapter", msg.toString()+" -> "+ (sent ? "right" : "left"));
        /* convertView must be created from scratch
            else convertView could have a 'wrong' layout
         */
        //if (convertView == null) {
            //int layoutID = R.layout.layout_message;
            int layoutID = sent ?
                    R.layout.layout_message_right :
                    R.layout.layout_message_left;
            convertView = LayoutInflater.from(mContext).inflate(layoutID, parent, false);
        //}
        TextView text = (TextView)convertView.findViewById(R.id.msg_text);
        TextView time = (TextView)convertView.findViewById(R.id.msg_date);
        TextView sender = (TextView) convertView.findViewById(R.id.msg_sender);
        ImageView photoView = (ImageView) convertView.findViewById(R.id.member_photo);
        /*if (recycledConvertView && text.getGravity() != gravity){
            text.setGravity(gravity);
            time.setGravity(gravity);
            //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) convertView.getLayoutParams();
            //params.gravity = gravity;
            //convertView.setLayoutParams(params);
        }
        */

        int idx = Math.abs(msg.senderUsername.hashCode()) % mColors.length;
        sender.setTextColor(mColors[idx]);
        sender.setText(msg.senderCompleteName);
        text.setText(msg.message);
        time.setText(mDateFormat.format(msg.timeStamp));

        /*byte[] photo = msg.photoBytes;
        if (photo != null){
            photoView.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
        }
        else photoView.setImageResource(R.drawable.student_icon);
        */

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
