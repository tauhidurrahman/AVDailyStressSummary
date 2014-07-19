package com.example.avdailystresssummary;



import android.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarViewActivity extends Activity {
	private String[] filenames;
	private Button next;
	GridView gridview;
	private TextView tv;
	String userID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_view);
		
		Intent intentfromMainActivity = getIntent();
		userID=intentfromMainActivity.getExtras().getString("UID");
		
		gridview = (GridView) findViewById(R.id.calendar_grid);
		tv = (TextView) findViewById(R.id.calendar_month);
		next= (Button) findViewById(R.id.calendar_next);
		next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//load next dates in grid, also change the calendar month if needed
				//setupPAM();
			}

		});

		// set up calendarView
		setupCalView();
	}

	/** setup PAM */
	private void setupCalView() {
		
		gridview.setAdapter(new MyGridAdapter (this));
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
				//gridview.getChildAt(position).setBackgroundColor(Color.RED);
				
				/*Intent intent = new Intent(getApplicationContext(), MapActivity.class);
				intent.putExtra("UID", userID);
				if (position < 9)
					intent.putExtra("Date", "0"+(position+1)+"_12_2012");
				else
					intent.putExtra("Date", (position+1) + "_12_2012");
		      	startActivity(intent);*/
				finish();
				
			}
		});
		
	}

}
