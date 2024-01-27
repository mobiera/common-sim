package com.mobiera.java.sim.tools;

public class CellLocation {
	
	
	private int mnc;
	private int mcc;
	private long lacTac;
	private long cellId;
	private double latitude;
	private double longitude;
	
	public CellLocation () {
		
	}
	
	
	
	public int getMnc() {
		return mnc;
	}

	public void setMnc(int mnc) {
		this.mnc = mnc;
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	

	public long getCellId() {
		return cellId;
	}

	public void setCellId(long cellId) {
		this.cellId = cellId;
	}

	

	public long getLacTac() {
		return lacTac;
	}


	public void setLacTac(long lacTac) {
		this.lacTac = lacTac;
	}



	public double getLatitude() {
		return latitude;
	}



	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}



	public double getLongitude() {
		return longitude;
	}



	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	
	
}
