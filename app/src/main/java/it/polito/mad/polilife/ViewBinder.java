package it.polito.mad.polilife;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by luigi on 07/12/15.
 */
public interface ViewBinder<T> {

    View createView(Context context, ViewGroup parent, T obj);

}
