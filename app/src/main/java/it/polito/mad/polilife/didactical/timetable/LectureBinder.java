package it.polito.mad.polilife.didactical.timetable;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import it.polito.mad.polilife.ViewBinder;
import it.polito.mad.polilife.didactical.LectureLayout;
import it.polito.mad.polilife.didactical.timetable.data.Lecture;
import it.polito.mad.polilife.didactical.timetable.data.Time;

/**
 * Created by luigi onSelectAppliedJobs 07/12/15.
 */
public class LectureBinder implements ViewBinder<Lecture> {
    @Override
    public View createView(Context context, ViewGroup bg, Lecture l) {
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
}
