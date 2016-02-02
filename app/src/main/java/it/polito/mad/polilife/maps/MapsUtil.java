package it.polito.mad.polilife.maps;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by luigi on 02/02/16.
 */
public class MapsUtil {

    private MapsUtil(){}

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

    public static LatLng getFirstGMapsAddress(Context context, String address){
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

}
