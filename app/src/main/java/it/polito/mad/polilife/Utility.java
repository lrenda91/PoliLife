package it.polito.mad.polilife;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import it.polito.mad.polilife.didactical.LectureLayout;
import it.polito.mad.polilife.didactical.timetable.data.Lecture;
import it.polito.mad.polilife.didactical.timetable.data.Time;
import it.polito.mad.polilife.maps.GMapsHintsDLTask;

/**
 * Created by luigi onSelectAppliedJobs 13/05/15.
 */
public class Utility {

    private Utility(){}

    public static boolean networkIsUp(Context context){
        ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifiInfo != null && wifiInfo.isConnectedOrConnecting()) ||
                (mobileInfo != null && mobileInfo.isConnectedOrConnecting());
    }


    public static LectureLayout getView(Context context, Lecture l) {
        LectureLayout ll = new LectureLayout(context, l,
                l.getCourse().getName(), l.getCourse().getProfessor().getName(), ""+l.getDayOfWeek(),
                l.getStartTime().toString(), l.getEndTime().toString(), l.getClassroom());
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Time start = l.getStartTime();
        Time end = l.getEndTime();
        int height_dip = end.difference(start);
        int height_px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, height_dip, metrics);
        int topMargin_dip = start.difference(Time.START_TIME);
        int topMargin_px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, topMargin_dip, metrics);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height_px);
        params.topMargin = topMargin_px;
        ll.setLayoutParams(params);
        return ll;
    }

    public static Bitmap getBitmap(byte[] rawData){
        if (rawData == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(rawData, 0, rawData.length);
    }

    public static int getColorID(Context context, String course){
        TypedArray ar = context.getResources().obtainTypedArray(
                R.array.androidcolors);
        int idx = Math.abs(course.hashCode()) % ar.length();
        int colorResID = ar.getResourceId(idx,0);
        ar.recycle();
        return colorResID;
    }


    public static void setupSpinnerWithHint(Context c, Spinner s, String[] items, String hint){
        String[] all = new String[items.length+1];
        all[0] = hint;
        for (int i=1;i<all.length;i++) all[i] = ""+items[i-1];
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                c,android.R.layout.simple_spinner_dropdown_item, all){
            @Override
            public boolean isEnabled(int position){
                return position > 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(spinnerArrayAdapter);
    }

}
