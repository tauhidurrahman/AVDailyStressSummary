package com.example.avdailystresssummary;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class StressCluster {
	private LatLng position;
	private int numSamples;
	private float value;
	private float weight;
	
	public StressCluster(StressLocTime pos) {
		position = new LatLng(pos.getLatitude(), pos.getLongitude());
		numSamples = 1;
		value = pos.getStress();
	}
	
	public boolean addPoint(StressLocTime pos) {
		LatLng relative = new LatLng(pos.getLatitude(), pos.getLongitude());
		Location locationA = new Location("point A");

		locationA.setLatitude(position.latitude);
		locationA.setLongitude(position.longitude);

		Location locationB = new Location("point B");

		locationB.setLatitude(relative.latitude);
		locationB.setLongitude(relative.longitude);

		float distance = locationA.distanceTo(locationB);
		if (distance < 5)
			return false;
		
		value = (value * numSamples + pos.getStress()) / (numSamples + 1);
		weight = ((weight * numSamples) + (pos.getStress() / numSamples)) / (numSamples + 1);
		numSamples++;
		return true;
	}
	
	public LatLng getPosition() {
		return position;
	}
	
	public int getSamples() {
		return numSamples;
	}
	
	public float getValue() {
		return value;
	}
	
	public float getWeight() {
		return weight;
	}
}
