package it.polito.mad.polilife.didactical.timetable.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Course implements Serializable{

	private String name;
	private Professor professor;
	private String description;
	
	private List<Lecture> lectures;
	
	public Course(String name, Professor professor, String description) {
		this.name = name;
		this.professor = professor;
		this.description = description;
		this.lectures = new LinkedList<Lecture>();
	}

	public String getName() {
		return name;
	}
	
	public Professor getProfessor() {
		return professor;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<Lecture> getLectures() {
		return lectures;
	}
	
	@Override
	public String toString() {
		return name+":"+ professor;
	}
}
