package it.polito.mad.polilife;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import it.polito.mad.polilife.didactical.LectureLayout;
import it.polito.mad.polilife.didactical.timetable.data.Lecture;
import it.polito.mad.polilife.didactical.timetable.data.Time;
import it.polito.mad.polilife.maps.GMapsHintsDLTask;

/**
 * Created by luigi on 13/05/15.
 */
public class Utility {

    private Utility(){}

    public static boolean networkIsUp(Context context){
        ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifiInfo.isConnected() || mobileInfo.isConnected();
    }

    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static LectureLayout getView(Context context, Lecture l) {
        LectureLayout ll = new LectureLayout(context, null,
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

    public static void slide_up(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
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

    public static int calculateZoomLevel(Activity context, int radiusInMeters) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double equatorLength = 40075004; // in meters
        double widthInPixels = metrics.widthPixels;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > radiusInMeters) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        return zoomLevel;
    }

    public static void setAutoCompleteGMaps(final AutoCompleteTextView actv){
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(actv.getContext(),
                android.R.layout.simple_list_item_1);
        adapter.setNotifyOnChange(true);

        actv.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count % 3 == 1) {
                    it.polito.mad.polilife.maps.GMapsHintsDLTask task = new GMapsHintsDLTask(actv.getContext(),
                            new GMapsHintsDLTask.HintsDownloadedCallback() {
                                @Override
                                public void onDownloadCompleted(List<String> result) {
                                    adapter.clear();
                                    adapter.addAll(result);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onDownloadError(Exception exception) {

                                }
                            });
                    task.execute(s.toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        actv.setAdapter(adapter);
    }

    public static LatLng getFirstAddress(Context context, String address){
        if (address == null){
            return null;
        }
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        if (addresses.isEmpty()){
            return null;
        }
        Address a = addresses.get(0);
        return new LatLng(a.getLatitude(), a.getLongitude());
    }

}
