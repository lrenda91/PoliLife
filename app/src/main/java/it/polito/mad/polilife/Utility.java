package it.polito.mad.polilife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

    public static Bitmap getBitmap(byte[] rawData){
        if (rawData == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(rawData, 0, rawData.length);
    }

}
