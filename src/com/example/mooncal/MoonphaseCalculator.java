package com.example.mooncal;

public class MoonphaseCalculator {
	public double[] phases;
	public int[] lunations;
	
	static {
		System.loadLibrary("moonphase");
	}
	
	private native void calcNative(int year, int month, double[] phi, int[] lun);
	protected MoonphaseCalculator() {
		final int daysMax=42;
		phases=new double[daysMax];
		lunations=new int[daysMax];
	}
	public void calc(int year, int month) {
		calcNative(year, month, phases, lunations);
	}
}
