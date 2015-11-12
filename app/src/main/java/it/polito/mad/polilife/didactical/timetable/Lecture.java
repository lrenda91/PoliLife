package it.polito.mad.polilife.didactical.timetable;

import java.io.Serializable;

public class Lecture implements Serializable {

	private Course course;
	private String classroom;
	private int dayOfWeek;
	private Time startTime, endTime;
	
	public Lecture(Course course, Time startTime, Time endTime, int dayOfWeek, String classroom) {
		this.course = course;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayOfWeek = dayOfWeek;
		this.classroom = classroom;
	}
	
	public Course getCourse() {
		return course;
	}

	public String getClassroom() {
		return classroom;
	}
	
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
	
	@Override
	public String toString() {
		return "{"+course.getName()+" on "+dayOfWeek+" "+startTime+"-"+endTime+" "+classroom+"}";
	}
	
}
