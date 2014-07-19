package com.example.avdailystresssummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.avdailystresssummary.ColorGridAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.GroundOverlay;
import com.example.avdailystresssummary.R;
import com.example.avdailystresssummary.StressLocTime;
import com.example.avdailystresssummary.StressMapper;
import com.example.avdailystresssummary.TimeSeekBarListener;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MapActivity extends Activity {

	private static String RECORDER_FOLDER = "Anthroposophia";
	private static final String LOG_TAG = "AudioRecordTest";

	private String date = "19_12_2012";
	private String user = "rifat";
	private Handler handler = new Handler();
	private MediaPlayer mPlayer = null;
	private String mFileName;
	private GoogleMap mmap;
	private SeekBar seekbar;
	private GridView grid;
	private TextView value;
	private ImageButton imgb1, imgb2, imgb3;
	private Runnable SliderMover;
	private GroundOverlay go;
	private int startHour = 7;
	private int intPerHour = 1;
	private SparseArray<StressLocTime> ic;
	private List<StressLocTime> points = new ArrayList<StressLocTime>();

	// keep track of marks on the map

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		/*----------------SETUP----------------*/
		value = (TextView) findViewById(R.id.textview);
		grid = (GridView) findViewById(R.id.color_grid);
		ColorGridAdapter cga = new ColorGridAdapter(this);
		grid.setAdapter(cga);
		ic = readDailySummary(date);
		grid.setNumColumns((24 - startHour + 1) * intPerHour);
		cga.updateCols((24 - startHour + 1) * intPerHour);
		cga.sample(ic);

		seekbar = (SeekBar) findViewById(R.id.seekbar);
		imgb1 = (ImageButton) findViewById(R.id.imageButton1);
		imgb1.setImageResource(R.drawable.back);
		imgb2 = (ImageButton) findViewById(R.id.imageButton2);
		imgb2.setImageResource(R.drawable.play);
		imgb3 = (ImageButton) findViewById(R.id.imageButton3);
		imgb3.setImageResource(R.drawable.forward);

		value.setTextSize(24);
		String min = "" + startHour;
		value.setText(min + ":00");
		setUpMapIfNeeded();

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/Anthroposophia/dailyStressAudio.wav";

		seekbar.setMax((24 - startHour) * intPerHour);
		seekbar.setOnSeekBarChangeListener(new TimeSeekBarListener(mmap, value,
				intPerHour, startHour, ic));

		SliderMover = new Runnable() {
			@Override
			public void run() {
				handler.postDelayed(SliderMover, 5000);
				if (seekbar.getProgress() < seekbar.getMax())
					seekbar.setProgress(seekbar.getProgress() + 1);
				else {
					stopPlaying();
					imgb2.setImageResource(R.drawable.play);
					seekbar.setEnabled(true);
					handler.removeCallbacks(SliderMover);
				}
			}
		};

		/*----------------ACTIONS WHEN THE 'PLAY' BUTTON IS PRESSED----------------*/
		imgb2.setOnClickListener(new OnClickListener() {
			boolean playing;

			@Override
			public void onClick(View arg0) {
				playing = !playing;
				if (playing) {
					startPlaying();
					imgb2.setImageResource(R.drawable.pause);
					seekbar.setEnabled(false);
					handler.postDelayed(SliderMover, 5000);
				} else {
					stopPlaying();
					imgb2.setImageResource(R.drawable.play);
					seekbar.setEnabled(true);
					handler.removeCallbacks(SliderMover);
				}
			}
		});

		imgb3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (go != null)
					go.remove();
				StressMapper sm = new StressMapper(20, mmap, points,
						((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getView());
				go = mmap.addGroundOverlay(sm.getOverlay());
			}
		});
	}

	/**
	 * Visualizes the MapFragment on the interface
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mmap == null) {
			mmap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mmap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				mmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
		}
	}

	private void startPlaying() {
		if (mPlayer == null) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(mFileName);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IOException e) {
				Log.e(LOG_TAG, "prepare() failed");
			}
		} else {
			/*
			 * if (refPos != tsbl.posRatio()) mPlayer.seekTo((int)
			 * (tsbl.posRatio() * mPlayer.getDuration()));
			 */
			mPlayer.start();
		}
	}

	private void stopPlaying() {
		mPlayer.pause();
	}

	/**
	 * Given a String representation of a date, returns a SparseArray containing
	 * all the StressLocTime points of that day stored in the memory
	 * 
	 * @param date
	 *            A string representation of the date of the data we want
	 *            (dd_mm_yyyy)
	 * @return A SparseArray of the points we want
	 */
	private SparseArray<StressLocTime> readDailySummary(String date) {
		// write on SD card file data
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File dir = new File(filepath, RECORDER_FOLDER);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, date + "_" + user + ".txt");
		SparseArray<StressLocTime> dailySummary = new SparseArray<StressLocTime>();

		try {
			Scanner inputStream = new Scanner(file);
			int i = 0;
			while (inputStream.hasNext()) {
				String line = inputStream.next().trim();
				String[] values = line.split(",");
				// Now put the values in the class
				StressLocTime s1 = new StressLocTime(values[0], values[1],
						values[2], values[3], values[4], "");
				if (i == 0) {
					startHour = Integer.parseInt("" + values[0].charAt(0) + ""
							+ values[0].charAt(1));
					i = 1;
				}
				points.add(s1);
				// Now add the class in the arraylist
				String[] hms = values[0].split(":");

				int time = (Integer.parseInt(hms[0]) * intPerHour - startHour + (Integer
						.parseInt(hms[1]) / (60 / intPerHour)));
				dailySummary.put(time, s1);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			StressLocTime s1 = new StressLocTime("FileNotFound", "", "", "",
					"", "");
		}
		return dailySummary;
	}

}
