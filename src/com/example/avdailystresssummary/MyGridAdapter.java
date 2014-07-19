package com.example.avdailystresssummary;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


public class MyGridAdapter extends BaseAdapter {
    private Context context;

    public MyGridAdapter(Context c) {
        context = c;
    }

    public int getCount() {
        return days31.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    static final String[] days30 = new String[] { 
		"1", "2", "3", "4", "5",
		"6", "7", "8", "9", "10",
		"11", "12", "13", "14", "15",
		"16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26","27","28","29","30"};
    static final String[] days31 = new String[] { 
		"1", "2", "3", "4", "5",
		"6", "7", "8", "9", "10",
		"11", "12", "13", "14", "15",
		"16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26","27","28","29","30","31"};
    static final String[] days28 = new String[] { 
		"1", "2", "3", "4", "5",
		"6", "7", "8", "9", "10",
		"11", "12", "13", "14", "15",
		"16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26","27","28"};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	tv = new TextView(context);
            //tv.setLayoutParams(new GridView.LayoutParams(50, 80));
            //tv.setTextSize(20);   //text size in gridview
        } else {
            tv = (TextView) convertView;
        }
        tv.setText(days31[position]);
        /*int c;
        if(position%2==0)
        	c = Color.RED;//this color should be estimated from stress
        else
        	c=Color.GREEN;
        	
        if(position==5)
        	tv.setBackgroundColor(Color.GREEN);*/
        return tv;
	}
}