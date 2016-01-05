package it.polito.mad.polilife.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.db.classes.Notice;

public class BookSearchActivity extends AppCompatActivity {

    private Notice.Filter params = new Notice.Filter();

    private AutoCompleteTextView locationEditText;
    private TextView within;
    private SeekBar minPriceSeekBar, maxPriceSeekBar, withinSeekBar;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        locationEditText = (AutoCompleteTextView) findViewById(R.id.location);
        Utility.setAutoCompleteGMaps(locationEditText);
        minPriceSeekBar = (SeekBar) findViewById(R.id.minPriceSeekBar);
        maxPriceSeekBar = (SeekBar) findViewById(R.id.maxPriceSeekBar);
        within = (TextView) findViewById(R.id.within);

        withinSeekBar = (SeekBar) findViewById(R.id.withinSeekBar);
        withinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                within.setText(String.valueOf(progress > 0 ? progress : 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        withinSeekBar.setProgress(1);
        withinSeekBar.setMax(10);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adv_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_filter:
                params.bookType();
                String locationName = locationEditText.getText().toString();
                params.location(locationName.isEmpty() ? null : locationName)
                        .minPrice(minPriceSeekBar.getProgress())
                        .maxPrice(maxPriceSeekBar.getProgress());

                LatLng coordinates = Utility.getFirstAddress(this, locationName);
                if (coordinates != null){
                    params.latitude(coordinates.latitude)
                            .longitude(coordinates.longitude)
                            .within(Integer.valueOf(within.getText().toString()));
                }

                Intent backIntent = new Intent();
                backIntent.putExtra("params", params);
                setResult(Activity.RESULT_OK, backIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, backIntent);
        finish();
    }

}
