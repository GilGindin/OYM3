package com.example.gilvi.oym;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {


    //class fields
    List<Video> videos;
    private static VideosAdapter adapter;
    private RecyclerView videosView;
    private LinearLayout backgroundLinearLayout;
    private int startColor = 0xFF00E5AB; //58795
    private int endColor = 0xFF000B42; //2882
    SeekBar seekBar;
    Spinner spinner1;
    String[] db;  // which artists db is currently in use - the 'general' of the 'Jazz'
    String genre; // which genre is currently selected in the spinner
    TextView textViewMin;
    TextView textViewMax;
    TextView textViewCurrent;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.videos = new ArrayList<>();
        this.backgroundLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_main);
        this.videosView = (RecyclerView) findViewById(R.id.recyclerView_videos);
        seekBar = (SeekBar) findViewById(R.id.seekBar_artists);

        //initially - select the 'general' db
        db = Consts.TOP_ARTISTS;

        //the the min and max TextViews (below the seekbar) to 0 and the DB size
        textViewMin = (TextView) findViewById(R.id.textView_min);
        textViewMin.setText(Integer.toString(0));
        textViewMax = (TextView) findViewById(R.id.textView_max);
        textViewMax.setText(Integer.toString(db.length));
        textViewCurrent = (TextView) findViewById(R.id.textView_current);
        textViewCurrent.setText(Integer.toString(0));

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //set the max value for the seekbar
        seekBar.setMax(db.length);

        // set seekbar color
        seekBar.getProgressDrawable().setColorFilter(0xffff4081, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(0xffff4081, PorterDuff.Mode.SRC_IN);

        // set Listener for seekbar value change.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //when seeking - this listener is called every time the value is changed
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {

                //change the background color
                float coef = (float) progress / (float) db.length;
                int red = Math.round(coef * 0 + coef * 0);
                int green = Math.round(coef * 229 + (1 - coef) * 11);
                int blue = Math.round(coef * 171 + (1 - coef) * 66);
                textViewCurrent.setText(Integer.toString(progress));
                backgroundLinearLayout.setBackgroundColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //when the finger leaves the seekbar circle this listener is called
            //we wait 2 seconds - and than make the call to youtube.
            //we wait 2 seconds because we don't want to initiate calls every time the user is touching the bar
            // only when he leave it for at least 2 seconds
            //if 2 seconds has passed without the user changing again - call the API to get youtube results
            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Utils.cancelDelay();
                Utils.delay(2, new Utils.DelayCallback() {
                    @Override
                    public void afterDelay() {
                        callApi(seekBar.getProgress());
                    }
                });
            }
        });
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        String[] genres = {"General", "Pop", "Rock", "Ethnic", "Jazz", "Hip-Hop", "Global Combined"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, genres);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //when changing genre - we change the DB accordingly (from the 5000 general to the 100 Jazz and vice versa)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    return;
                }
                genre = ((TextView) view).getText().toString();
                if (genre.equals("Jazz")) {
                    db = Consts.TOP_100_JAZZ_ARTISTS;
                } else if (genre.equals("Global Combined")){
                    db = Consts.TOP_5000_COMBINED_ARTISTS;
                } else {
                    db = Consts.TOP_ARTISTS;
                }
                seekBar.setProgress(0);
                seekBar.setMax(db.length);
                textViewMax.setText(Integer.toString(db.length));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void callApi(int artistsLimit) {
        progressBar.setVisibility(View.VISIBLE);
        Api.callApi(new ResponseListener() {
            @Override
            public void onFinished(List<Video> result) {
                MainActivity.this.videos = result;
                MainActivity.this.refreshVideosUI();
                progressBar.setVisibility(View.GONE);
            }
        }, artistsLimit, spinner1.getSelectedItem().toString());
    }

    private void refreshVideosUI() {
        adapter = new VideosAdapter((ArrayList<Video>) videos, getApplicationContext());
        this.updateViewWithData();
    }

    public void updateViewWithData() {

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView_videos);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        VideosAdapter adapter = new VideosAdapter(
                new ArrayList<Video>(this.videos), this
        );

        rv.setAdapter(adapter);

    }
}

