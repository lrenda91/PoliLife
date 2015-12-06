package it.polito.mad.polilife.didactical.timetable.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.annotation.SuppressLint;

public class TimetableImpl implements Timetable {

	private Map<String, Course> coursesByName;

	private TimetableImpl() {
		coursesByName = new HashMap<>();
	}

	@Override
	public Collection<Course> getCourses() {
		return coursesByName.values();
	}

	@Override
	public Collection<Lecture> getLectures() {
		List<Lecture> result = new LinkedList<Lecture>();
		for (Course course : coursesByName.values()) {
			result.addAll(course.getLectures());
		}
		return result;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public Collection<Lecture> filter(String courseName, String teacherName) {
		List<Lecture> result = new LinkedList<Lecture>();
		for (Course course : coursesByName.values()) {
			boolean nameMatches = (courseName == null || course.getName()
					.toLowerCase().contains((courseName.toLowerCase())));
			boolean teacherMatches = (teacherName == null || course
					.getProfessor().getName().toLowerCase()
					.contains(teacherName.toLowerCase()));
			if (!nameMatches || !teacherMatches) {
				continue;
			}
			result.addAll(course.getLectures());
		}
		return result;
	}

	public static TimetableImpl newInstance(InputStream coursesFileStream,
											InputStream timetableFileStream) {
		TimetableImpl timetable = new TimetableImpl();
		try {
			String content = "";
			String l = null;
			BufferedReader br = null;
			JSONObject jsonObject = null;

			br = new BufferedReader(new InputStreamReader(
					coursesFileStream));
			while ((l = br.readLine()) != null) {
				content += l;
			}
			jsonObject = (JSONObject) new JSONParser().parse(content);

            HashMap<String, Professor> profMap = new HashMap<>();
            JSONArray teachersArray = (JSONArray) jsonObject.get("teacher");
            for (int i = 0; i < teachersArray.size(); i++) {
                JSONObject courseObj = (JSONObject) teachersArray.get(i);
                String ID = (String) courseObj.get("ID");
                String name = (String) courseObj.get("name");
                String mail = (String) courseObj.get("email");
                String phone = (String) courseObj.get("phone");
                String office = (String) courseObj.get("office");
                Professor p = new Professor(ID, name, mail, phone, office);
                profMap.put(ID, p);
            }

			JSONArray coursesArray = (JSONArray) jsonObject.get("courses");
			for (int i = 0; i < coursesArray.size(); i++) {
				JSONObject courseObj = (JSONObject) coursesArray.get(i);
				String name = (String) courseObj.get("name");
				String teacher = (String) courseObj.get("teacher");
				String description = (String) courseObj.get("description");
				Course course = new Course(name, profMap.get(teacher), description);
				timetable.coursesByName.put(name, course);
			}

            content = "";
            br = new BufferedReader(new InputStreamReader(
                    timetableFileStream));
            while ((l = br.readLine()) != null) {
                content += l;
            }
            jsonObject = (JSONObject) new JSONParser().parse(content);

			JSONArray lecturesArray = (JSONArray) jsonObject.get("lectures");
			for (int i = 0; i < lecturesArray.size(); i++) {
				JSONObject lectureObj = (JSONObject) lecturesArray.get(i);
				long day = (Long) lectureObj.get("day");
				String classroom = (String) lectureObj.get("classroom");
				String start = (String) lectureObj.get("start");
				String end = (String) lectureObj.get("end");
				String courseName = (String) lectureObj.get("course");
				StringTokenizer st = null;
				st = new StringTokenizer(start, ":");
				Time startTime = new Time(Integer.parseInt(st.nextToken()),
						Integer.parseInt(st.nextToken()));
				st = new StringTokenizer(end, ":");
				Time endTime = new Time(Integer.parseInt(st.nextToken()),
						Integer.parseInt(st.nextToken()));
				Course myCourse = timetable.coursesByName.get(courseName);
				myCourse.getLectures().add(
						new Lecture(myCourse, startTime, endTime, (int)day,
								classroom));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return timetable;
	}
	
}
