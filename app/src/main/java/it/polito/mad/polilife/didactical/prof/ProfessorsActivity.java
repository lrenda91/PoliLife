package it.polito.mad.polilife.didactical.prof;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Professor;

public class ProfessorsActivity extends AppCompatActivity
        implements DBCallbacks.MultipleFetchCallback<Professor> {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professors);

        mListView = (ListView) findViewById(R.id.professors_list);

        PoliLifeDB.downloadProfessorsInfo(this);
    }

    @Override
    public void onFetchSuccess(final List<Professor> result) {
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return result.size();
            }

            @Override
            public Professor getItem(int position) {
                return result.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                /*if (convertView == null) {
                    convertView = LayoutInflater.from(ProfessorsActivity.this)
                            .inflate(R.layout.layout_classroom_result_item, parent, false);
                }
*/
                final Professor item = getItem(position);

                return null;
            }
        });
    }

    @Override
    public void onFetchError(Exception exception) {

    }

}
