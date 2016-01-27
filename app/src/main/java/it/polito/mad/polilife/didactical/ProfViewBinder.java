package it.polito.mad.polilife.didactical;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.ViewBinder;
import it.polito.mad.polilife.db.classes.Professor;

/**
 * Created by luigi onSelectAppliedJobs 07/12/15.
 */
public class ProfViewBinder implements ViewBinder<Professor> {
    @Override
    public View createView(Context context, ViewGroup parent, Professor obj) {
        View result = LayoutInflater.from(context)
                .inflate(R.layout.layout_professor_item, parent, false);
        return null;
    }
}
