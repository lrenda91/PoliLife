package it.polito.mad.polilife.placement;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.classes.Job;

/**
 * Created by luigi onSelectAppliedJobs 27/12/15.
 */
public class PositionsBaseAdapter extends BaseAdapter {

    interface OnJobClickListener {
        void onPositionItemClick(View itemView, int position, Job item);
    }

    private Context mContext;
    private List<Job> mData;
    private OnJobClickListener mOnClickListener;

    public PositionsBaseAdapter(Context context){
        mContext = context;
        mData = new LinkedList<>();
    }

    public void setOnClickListener(OnJobClickListener listener) {
        this.mOnClickListener = listener;
    }

    public void setData(List<Job> data){
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Job getItem(int position) {
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
                    R.layout.layout_position_item, parent, false);
        }
        final Job p = getItem(position);
        Resources res = mContext.getResources();
        DateFormat df = new SimpleDateFormat(res.getString(R.string.date_format));
        String name = p.getName() != null ? p.getName() : res.getString(R.string.no_name);
        String city = p.getCity() != null ? p.getCity() : res.getString(R.string.no_city);
        String date = p.getStartDate() != null ? df.format(p.getStartDate()) : res.getString(R.string.no_start_date);
        ((TextView) convertView.findViewById(R.id.offer_title)).setText(name);
        ((TextView)convertView.findViewById(R.id.offer_location)).setText(city);
        ((TextView)convertView.findViewById(R.id.offer_date)).setText(date);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null){
                    mOnClickListener.onPositionItemClick(v, position, p);
                }
            }
        });
        return convertView;
    }

}
