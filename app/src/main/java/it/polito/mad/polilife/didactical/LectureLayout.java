package it.polito.mad.polilife.didactical;

import android.widget.RelativeLayout;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.widget.TextView;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.didactical.timetable.LectureDetailsActivity;
import it.polito.mad.polilife.didactical.timetable.data.Lecture;

public class LectureLayout extends RelativeLayout {
	
	//private String courseName, teacherName, schedule, classroomName;
	//private int colorResID;
	
	public LectureLayout(Context context, final Lecture lecture, String course, String teacher,
			String day, String time1, String time2, String classroom) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.layout_lecture, this);

		/*courseName = course;
		teacherName = teacher;
		schedule = day+" from "+time1+" to "+time2;
		classroomName = classroom;*/
		
		((TextView) findViewById(R.id.textView1)).setText(lecture.getCourse().getName());

		final int colorResID = Utility.getColorID(this.getContext(), course);
		setBackgroundColor(getResources().getColor(colorResID));
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), LectureDetailsActivity.class);
				intent.putExtra("lecture", lecture);
				intent.putExtra("color", colorResID);
				/*intent.putExtra("course", courseName);
				intent.putExtra("teacher", teacherName);
				intent.putExtra("time", schedule);
				intent.putExtra("color", caller.colorResID);
				intent.putExtra("room", caller.classroomName);*/
				getContext().startActivity(intent);
			}
		});
	}
	
}
