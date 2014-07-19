package com.example.avdailystresssummary;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ColorGridAdapter extends BaseAdapter {
	private Context context;
	private int numCols;
	private int[] colors;

	public ColorGridAdapter(Context c) {
		context = c;
	}

	public void updateCols(int numColumns) {
		numCols = numColumns;
	}

	public void sample(SparseArray<StressLocTime> vals) {
		colors = new int[numCols];
		for (int i = 0; i < numCols; i++) {
			if (vals.get(i) != null) {
				float[] hsv = new float[3];
				hsv[0] = vals.get(i).getStress() * 140 + 220;
				hsv[1] = 1;
				hsv[2] = 1;
				colors[i] = Color.HSVToColor(hsv);
			} else
				colors[i] = Color.WHITE;
		}
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			tv = new TextView(context);
			// tv.setLayoutParams(new GridView.LayoutParams(100, 80));
			// tv.setTextSize(20); //text size in gridview
		} else {
			tv = (TextView) convertView;
		}
		tv.setBackgroundColor(colors[position]);
		return tv;
	}

	@Override
	public int getCount() {
		return numCols;
	}
}