package com.example.mooncal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MoonDayView extends View {
	private Path mPath;
	private Path fPath;
	private Paint mPaint,fPaint;
	private RectF outerBounds;
	private double moonPhase=0.;
	private int lunation=0;
	
	public MoonDayView(Context context) {
		super(context);
		prepareDrawable();
	}
	
	public MoonDayView(Context context, AttributeSet attrs) {
		super(context,attrs);
		prepareDrawable();
	}
	
	public void setPhaseLunation(double phase, int lun) {
		moonPhase=phase;
		lunation=lun;
		invalidate();
	}
	
	private void prepareDrawable() {
		Resources r=getResources();
		mPath=new Path();
		fPath=new Path();
		mPaint=new Paint();
		mPaint.setColor(r.getColor(R.color.moonColor));
		mPaint.setAntiAlias(true);
		fPaint=new Paint();
		fPaint.setStyle(Paint.Style.STROKE);
		fPaint.setColor(r.getColor(R.color.frameColor));
		fPaint.setAntiAlias(true);
		outerBounds=new RectF(0,0,256.f,256.f);
	}
	
	private void setupPath(int left, int top, int radius) {
		outerBounds.set(left,top,left+radius,top+radius);
		mPath.reset();
		fPath.reset();
		if(lunation>=0) {
			if(lunation==0) { // New Moon.
				fPath.addArc(outerBounds, 0, 360);
				fPath.close();
			} else if(lunation==1) { // Second quarter
				mPath.addArc(outerBounds, 270, 180);
				mPath.close();
				fPath.set(mPath);
			} else if(lunation==2) { // Full moon
				mPath.addOval(outerBounds, Path.Direction.CCW);
				mPath.close();
				fPath.set(mPath);
			} else if(lunation==3) { // Third quarter
				mPath.addArc(outerBounds, 90, 180);
				mPath.close();
				fPath.set(mPath);
			}
			fPaint.setStrokeWidth(radius/8);
		} else {
			double fraction=moonPhase-Math.floor(moonPhase);
			double theta;
			double ovalWidth;
			int halfArcStart;
			int semiArcStart;
			int semiArcAngles;
			
			if(moonPhase<2.) {
				halfArcStart=270;
				semiArcStart=90;
				if(moonPhase>=1.) {
					semiArcAngles=180;
					fraction = 1.-fraction;
				} else {
					semiArcAngles=-180;
				}
			} else {
				halfArcStart=90;
				semiArcStart=270;
				if(moonPhase<=3.) {
					semiArcAngles=180;
				} else {
					fraction=1.-fraction;
					semiArcAngles=-180;
				}
			}
			mPath.addArc(outerBounds, halfArcStart, 180);
			theta=(Math.PI/2.)*fraction;
			ovalWidth=Math.cos(theta)*radius;
			outerBounds.set((float) (left+(radius-ovalWidth)/2.), top,
					(float) (left+(radius-ovalWidth)/2.+ovalWidth), top+radius);
			mPath.addArc(outerBounds, semiArcStart, semiArcAngles);
			mPath.close();
		}
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int h=canvas.getHeight();
		int w=canvas.getWidth();
		int r=Math.min(w, h);
		int ovalR=(80*r)/100;
		int x=(w-ovalR)/2;
		int y=(h-ovalR)/2;
		
		setupPath(x,y,ovalR);
		if(lunation>=0) {
			canvas.drawPath(fPath,fPaint);
		}
		canvas.drawPath(mPath, mPaint);
	}
}
