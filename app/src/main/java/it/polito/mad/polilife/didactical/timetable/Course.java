package it.polito.mad.polilife.didactical.timetable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Course implements Serializable{

	private String name;
	private String teacher;
	private String description;
	
	private List<Lecture> lectures;
	
	public Course(String name, String teacher, String description) {
		this.name = name;
		this.teacher = teacher;
		this.description = description;
		this.lectures = new LinkedList<Lecture>();
	}

	public String getName() {
		return name;
	}
	
	public String getTeacher() {
		return teacher;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<Lecture> getLectures() {
		return lectures;
	}
	
	@Override
	public String toString() {
		return name+":"+teacher;
	}
}
