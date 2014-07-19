package com.example.avdailystresssummary;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.os.Environment;
import android.view.View;

public class StressMapper {

	private Bitmap layer;
	private Canvas canvas;
	private float radius;
	private GoogleMap mmap;
	private int width, height;
	private Paint p;
	private Projection proj;
	private LatLngBounds.Builder bc;

	public StressMapper(float radius, GoogleMap mmap,
			List<StressLocTime> points, View m) {
		this.radius = radius;
		this.mmap = mmap;
		p = new Paint();
		p.setStyle(Paint.Style.FILL);
		bc = new LatLngBounds.Builder();
		for (StressLocTime slt : points)
			bc.include(new LatLng(slt.getLatitude(), slt.getLongitude()));
		mmap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 0));
		proj = mmap.getProjection();
		p.setColor(Color.TRANSPARENT);
		width = m.getWidth();
		height = m.getHeight();
		/*
		 * width = Math.abs(proj.toScreenLocation(llb.southwest).x -
		 * proj.toScreenLocation(llb.northeast).x); height =
		 * Math.abs(proj.toScreenLocation(llb.southwest).y -
		 * proj.toScreenLocation(llb.northeast).y);
		 */
		layer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(layer);

		for (StressLocTime pt : points) {
			LatLng coords = new LatLng(pt.getLatitude(), pt.getLongitude());
			Point x = proj.toScreenLocation(coords);
			addPoint(x.x, x.y, pt.getStress());
		}
		colorize(0,0);
		save();
	}

	private void save() {
		Bitmap photo = layer;

		// transferring the Bitmap to byte[]
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		String filePath = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "Anthroposophia" + File.separator
				+ "Circles.jpeg";

		// Bitmap largeBitmap ; // save your Bitmap from data[]
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bos = null;
		int quality = 100;

		File pictureFile = new File(filePath);

		try {
			fileOutputStream = new FileOutputStream(pictureFile);
			bos = new BufferedOutputStream(fileOutputStream);
			photo.compress(CompressFormat.JPEG, quality, bos);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (bos != null) {
			try {
				bos.close();
			} catch (IOException e) {
				// ignore close error
			}
		}
	}

	public GroundOverlayOptions getOverlay() {
		LatLng topright = proj.fromScreenLocation(new Point(0, 0));
		float widthInMeters = distFrom(topright,
				proj.fromScreenLocation(new Point(width, 0)));
		return new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromBitmap(layer)).anchor(0, 0)
				.bearing(0).position(topright, widthInMeters);
	}

	private float distFrom(LatLng loc1, LatLng loc2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(loc2.latitude - loc1.latitude);
		double dLng = Math.toRadians(loc2.longitude - loc1.longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(loc1.latitude))
				* Math.cos(Math.toRadians(loc2.latitude)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (float) (dist * meterConversion);
	}

	private void addPoint(float x, float y, float times) {
		RadialGradient g = new RadialGradient(x, y, radius, Color.argb(
				(int) (255 * times), 0, 0, 0), Color.TRANSPARENT,
				TileMode.CLAMP);
		Paint gp = new Paint();
		gp.setShader(g);
		canvas.drawCircle(x, y, radius, gp);
	}

	private void colorize(float x, float y) {
		int[] pixels = new int[(int) (this.width * this.height)];
		layer.getPixels(pixels, 0, this.width, 0, 0, this.width, this.height);

		for (int i = 0; i < pixels.length; i++) {
			int r = 0, g = 0, b = 0, tmp = 0;
			int alpha = pixels[i] >>> 24;
			if (alpha == 0) {
				continue;
			}
			if (alpha <= 255 && alpha >= 235) {
				tmp = 255 - alpha;
				r = 255 - tmp;
				g = tmp * 12;
			} else if (alpha <= 234 && alpha >= 200) {
				tmp = 234 - alpha;
				r = 255 - (tmp * 8);
				g = 255;
			} else if (alpha <= 199 && alpha >= 150) {
				tmp = 199 - alpha;
				g = 255;
				b = tmp * 5;
			} else if (alpha <= 149 && alpha >= 100) {
				tmp = 149 - alpha;
				g = 255 - (tmp * 5);
				b = 255;
			} else
				b = 255;
			pixels[i] = Color.argb((int) alpha / 2, r, g, b);
		}
		layer.setPixels(pixels, 0, this.width, 0, 0, this.width, this.height);
	}
}
