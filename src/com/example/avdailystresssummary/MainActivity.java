package com.example.avdailystresssummary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EditText uId = (EditText) findViewById(R.id.editTextID);
		
		Intent intent = new Intent(this, CalendarViewActivity.class);
		intent.putExtra("UID", uId.getText().toString());
      	startActivity(intent);
	}


}
