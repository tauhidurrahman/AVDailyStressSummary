package com.example.avdailystresssummary;

public class StressLocTime {
	
	private String hh_mm_ss;
	private double longitude;
	private double latitude;
	private float stressLevel;
	private String noise;
	private String activity;
	
	// Constructor
	public StressLocTime(String hms,String lon,String lat,String str, String n, String act){
		this.hh_mm_ss=hms;
		this.longitude=Double.parseDouble(lon);
		this.latitude=Double.parseDouble(lat);
		this.stressLevel=Float.parseFloat(str);
		this.noise=n;
		this.activity=act;
	}
	
	// Accessor Methods
	public String getHHMMSS(){
		return this.hh_mm_ss;
	}
	public double getLongitude(){
		return this.longitude;
	}
	public double getLatitude(){
		return this.latitude;
	}
	public float getStress(){
		return this.stressLevel;
	}
	public String getNoise(){
		return this.noise;
	}
	public String getActivity(){
		return this.activity;
	}
}
