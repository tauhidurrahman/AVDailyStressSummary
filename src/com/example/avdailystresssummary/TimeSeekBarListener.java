package com.example.avdailystresssummary;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.graphics.Color;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.util.SparseArray;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TimeSeekBarListener implements OnSeekBarChangeListener {

	private Handler handler = new Handler();
	private Runnable Zoomer;
	private GoogleMap mmap;
	private double posRatio;
	private List<Marker> markers;
	private List<Circle> circles;
	private List<Polyline> lines;
	private TextView value;
	private SparseArray<StressLocTime> ic;
	private LatLng prev, cur;
	private int curColor, intPerHour, startHour;
	

	public TimeSeekBarListener(GoogleMap map, TextView val, int intPerHour, int startHour, SparseArray<StressLocTime> ic) {
		this.ic = ic;
		this.intPerHour = intPerHour;
		this.startHour = startHour;
		mmap = map;
		Zoomer = new Runnable() {
			@Override
			public void run() {
				if (mmap.getCameraPosition().zoom >= 19)
					mmap.animateCamera(CameraUpdateFactory.newLatLngZoom(cur,
							19));
				else {
					for (Circle x : circles)
						x.setRadius(Math.pow(2,
								(19 - mmap.getCameraPosition().zoom) + 1));
				}

			}
		};

		circles = new ArrayList<Circle>();
		markers = new ArrayList<Marker>();
		lines = new ArrayList<Polyline>();
		value = val;
		
		// Creates any markers for the default time of 12:00 AM
		if (ic.get(0) != null) {
			StressLocTime x = ic.get(0);
			prev = new LatLng(x.getLatitude(), x.getLongitude());
			markers.add(mmap.addMarker(new MarkerOptions()
					.position(prev)
					.title(x.getNoise())
					.snippet("Stress Level: " + x.getStress())
					.icon(BitmapDescriptorFactory.defaultMarker(x.getStress() * 140 + 220))));
			float[] hsv = new float[3];
			hsv[0] = x.getStress() * 140 + 220;
			hsv[1] = 1;
			hsv[2] = 1;
			int alpha = Color.alpha(Color.HSVToColor(hsv)) / 2;
			curColor = Color.HSVToColor(alpha, hsv);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(prev, 19));
			circles.add(mmap.addCircle(new CircleOptions().center(prev)
					.radius(2).strokeColor(Color.HSVToColor(0, hsv))
					.fillColor(curColor)));
		}
	}

	/**
	 * When the seekbar is changed, we change the time displayed, the markings
	 * displayed, and the camera position/zoom
	 */
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// FRAMING VARIABLES
		LatLngBounds.Builder bc = new LatLngBounds.Builder();

		/*----------------CLEAR THE SCREEN----------------*/
		for (Marker m : markers)
			m.remove();
		markers = new ArrayList<Marker>();
		for (Circle c : circles)
			c.remove();
		circles = new ArrayList<Circle>();
		for (Polyline l : lines)
			l.remove();
		lines = new ArrayList<Polyline>();

		/*----------------DISPLAYING TIME FROM SLIDER----------------*/
		int hour, minute;
		hour = progress / intPerHour + startHour;
		minute = progress % intPerHour * (60 / intPerHour);
		if (minute > 0)
			value.setText("" + hour + ":" + minute);
		else
			value.setText("" + hour + ":00");

		/*----------------CREATING THE MARKERS FROM DATA POINTS----------------*/
		int count = 5; // the number of stress indicators from previous
						// times that we want to see
		prev = null;
		for (int i = progress; i >= 0 && count > 0; i--) {

			if (ic.get(i) != null) {
				StressLocTime x = ic.get(i);
				cur = new LatLng(x.getLatitude(), x.getLongitude());

				// CREATES THE MARKER FOR THE CURRENT LOCATION
				if (i == progress) {
					markers.add(mmap.addMarker(new MarkerOptions()
							.position(cur)
							.title(x.getNoise())
							.snippet("Stress Level: " + x.getStress())
							.icon(BitmapDescriptorFactory.defaultMarker(x
									.getStress() * 140 + 220))));
					float[] hsv = new float[3];
					hsv[0] = x.getStress() * 140 + 220;
					hsv[1] = 1;
					hsv[2] = 1;
					int alpha = Color.alpha(Color.HSVToColor(hsv)) / 2;
					curColor = Color.HSVToColor(alpha, hsv);
					circles.add(mmap.addCircle(new CircleOptions().center(cur)
							.radius(2).strokeColor(Color.HSVToColor(0, hsv))
							.fillColor(curColor)));

				}
				// CREATES CIRCLES FOR PREVIOUS LOCATIONS
				else {
					float[] hsv = new float[3];
					hsv[0] = x.getStress() * 140 + 220;
					hsv[1] = 1;
					hsv[2] = 1;
					int alpha = Color.alpha(Color.HSVToColor(hsv)) / 2;
					circles.add(mmap.addCircle(new CircleOptions().center(cur)
							.radius(2).strokeColor(Color.HSVToColor(0, hsv))
							.fillColor(Color.HSVToColor(alpha, hsv))));
				}
			}

			// EXTRAPOLATES INFORMATION ON A DATA POINT IF MISSING BUT
			// HAS NEIGHBORING DATA POINTS
			else {
				for (int j = i + 1; j < 144; j++) {
					if (ic.get(j) != null) {
						for (int k = i - 1; k >= 0; k--) {
							if (ic.get(k) != null) {
								StressLocTime x = ic.get(j);
								StressLocTime y = ic.get(k);
								float avg = (x.getStress() * (j - i) + y
										.getStress() * (i - k))
										/ (j - k);
								double latAvg = (x.getLatitude() * (j - i) + y
										.getLatitude() * (i - k))
										/ (j - k);
								double lonAvg = (x.getLongitude() * (j - i) + y
										.getLongitude() * (i - k))
										/ (j - k);
								cur = new LatLng(latAvg, lonAvg);

								// CREATES THE MARKER FOR THE CURRENT LOCATION
								if (i == progress) {
									markers.add(mmap
											.addMarker(new MarkerOptions()
													.position(cur)
													.title("Extrapolated Data")
													.snippet(
															"Stress Level: "
																	+ avg)
													.icon(BitmapDescriptorFactory
															.defaultMarker(avg * 140 + 220))));
									mmap.moveCamera(CameraUpdateFactory
											.newLatLng(cur));
									int alpha = Color.alpha(Color.BLACK) / 2;
									curColor = Color.argb(alpha, 255, 255, 255);
									circles.add(mmap
											.addCircle(new CircleOptions()
													.center(cur)
													.radius(2)
													.strokeColor(
															Color.argb(0, 255,
																	255, 255))
													.fillColor(curColor)));
								}

								// CREATES CIRCLES FOR PREVIOUS LOCATIONS
								else {
									int alpha = Color.alpha(Color.BLACK) / 2;
									circles.add(mmap
											.addCircle(new CircleOptions()
													.center(cur)
													.radius(2)
													.strokeColor(
															Color.argb(0, 255,
																	255, 255))
													.fillColor(
															Color.argb(alpha,
																	255, 255,
																	255))));
								}
								break;
							}
						}
						break;
					}
				}
			}

			// IF WE HAVE A PREVIOUS MARKING, DRAW A PATH TO THAT MARKING, ADD
			// MARKING TO BUILDER
			if (prev != null && !prev.equals(cur))
				lines.add(mmap.addPolyline(new PolylineOptions().add(prev)
						.add(cur)));
			if (cur != null)
				bc.include(cur);

			// SET THE DESTINATION FOR NEXT CIRCLE
			prev = cur;

			count--;
		}

		// FRAMING OF THE CAMERA TO FIT ALL THE POINTS
		if (cur != null) {
			mmap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),
					50));
			handler.postDelayed(Zoomer, 2000);
		}
		posRatio = 1.0 * progress / seekBar.getMax();
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	public double posRatio() {
		return posRatio;
	}
}
