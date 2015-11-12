package it.polito.mad.polilife.didactical.timetable;

import java.io.Serializable;
import java.util.Collection;

public interface Timetable extends Serializable {

	public static final String DAYS[] = {
		"monday",
		"tuesday",
		"wednesday",
		"thursday",
		"friday"
	};
	
	Collection<Course> getCourses();
	
	Collection<Lecture> getLectures();
	
	Collection<Lecture> filter(String courseName, String teacherName);
	
}
