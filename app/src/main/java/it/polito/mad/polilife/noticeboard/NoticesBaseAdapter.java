package it.polito.mad.polilife.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Notice;

/**
 * Created by luigi onSelectAppliedJobs 26/12/15.
 */
public class NoticesBaseAdapter extends BaseAdapter {

    interface onNoticeClickListener {
        void onClick(View itemView, int position);
    }

    interface onNoticeLongClickListener {
        void onClick(View itemView, int position);
    }

    private Context mContext;
    private List<Notice> mData;
    private onNoticeClickListener mListener;
    private onNoticeLongClickListener mLongListener;
    private DateFormat mDateFormat;

    public NoticesBaseAdapter(Context context){
        mContext = context;
        mData = new ArrayList<>();
        mDateFormat = new SimpleDateFormat(context.getString(R.string.date_format));
    }

    public void setData(List<Notice> data){
        mData = data;
    }

    public void setOnNoticeClickListener(onNoticeClickListener listener){
        mListener = listener;
    }

    public void setOnNoticeLongClickListener(onNoticeLongClickListener listener){
        mLongListener = listener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Notice getItem(int position) {
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
                    R.layout.layout_notice_item, parent, false);
        }
        final Notice n = getItem(position);
        String title = n.getTitle() != null ? n.getTitle() : mContext.getString(R.string.no_title);
        String location = n.getLocationName() != null ? n.getLocationName() :
                mContext.getString(R.string.no_location);
        String from = n.getAvailableFrom() != null ? mDateFormat.format(n.getAvailableFrom()) :
                mContext.getString(R.string.no_available_from);
        ((TextView)convertView.findViewById(R.id.offer_title)).setText(title);
        ((TextView)convertView.findViewById(R.id.offer_location)).setText(location);
        ((TextView)convertView.findViewById(R.id.offer_date)).setText(from);
        ImageView photoIV = (ImageView) convertView.findViewById(R.id.preferred_photo);
        List<ParseFile> photos = n.getPhotos();
        if (photos != null && !photos.isEmpty()){
            try {
                byte[] bytes = photos.get(0).getData();
                photoIV.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
            catch (ParseException e){
                photoIV.setImageResource(R.drawable.ic_mail);
            }
        }
        else {
            photoIV.setImageResource(R.drawable.ic_mail);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(v, position);
                }
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongListener != null){
                    mLongListener.onClick(v, position);
                }
                return false;
            }
        });
        return convertView;
    }
}
