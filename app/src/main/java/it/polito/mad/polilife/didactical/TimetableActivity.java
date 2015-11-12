package it.polito.mad.polilife.didactical;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.didactical.timetable.Timetable;
import it.polito.mad.polilife.didactical.timetable.TimetableImpl;

public class TimetableActivity extends AppCompatActivity {

    private Timetable data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        if (savedInstanceState == null){
            InputStream lecturesStream = null, coursesStream = null;
            try {
                lecturesStream = getAssets().open("timetable.json");
                coursesStream = getAssets().open("courses.json");
            } catch (IOException e) {}
			/* Loading model data from JSON file */
            data = TimetableImpl.newInstance(coursesStream, lecturesStream);
        }
        else{
            data = (Timetable) savedInstanceState.getSerializable("model");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putSerializable("model", data);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            data = (Timetable) savedInstanceState.getSerializable("model");
        }
    }

}
