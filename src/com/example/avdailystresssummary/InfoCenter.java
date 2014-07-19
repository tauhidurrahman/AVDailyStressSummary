package com.example.avdailystresssummary;

public class InfoCenter {
	
	private double latitude;
	private double longitude;
	private String description;
	private int value;
	
	public InfoCenter(double lat, double lon, String des, int value) {
		latitude = lat;
		longitude = lon;
		description = des;		
		this.value = value;
	}
	
	public double[] getCoords() {
		double[] ans = new double[2];
		ans[0] = latitude;
		ans[1] = longitude;
		return ans;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getValue() {
		return value;
	}
	
	public void decrease() {
		value--;
	}
}
