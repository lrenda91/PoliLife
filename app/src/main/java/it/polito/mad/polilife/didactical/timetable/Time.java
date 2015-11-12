package it.polito.mad.polilife.didactical.timetable;

import java.io.Serializable;

public class Time implements Serializable {

	public static final Time START_TIME = new Time(8,30);
	public static final Time END_TIME = new Time(19,30);
	
	private int hour, minute;
	
	public Time(int hour, int minute) {
		if (hour < 0 || hour > 23 || minute < 0 || minute > 59){
			throw new IllegalArgumentException();
		}
		this.hour = hour;
		this.minute = minute;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public int difference(Time other){
		int myMins = hour*60 + minute;
		int otherMins = other.hour*60 + other.minute;
		return Math.abs(myMins-otherMins);
	}
	
	@Override
	public String toString() {
		return String.format("%02d", hour)+":"+String.format("%02d", minute);
	}
	
}
