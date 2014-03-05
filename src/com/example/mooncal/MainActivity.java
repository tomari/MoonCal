package com.example.mooncal;

import java.util.GregorianCalendar;

import android.os.Bundle;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView[] dayLabels;
	private MoonDayView[] moonViews;
	private String monthYearFormat;
	private GregorianCalendar monthShown;
	private MoonphaseCalculator mPC;
	private static final String STATE_YEAR="year";
	private static final String STATE_MONTH="month";
	private GestureDetector gestureD;
	private GestureListener gestureL;
	private ValueAnimator anim=null;
	
	static private final int[] dayNumberLabelIds = {
		R.id.dayNumberLabel00,R.id.dayNumberLabel01,R.id.dayNumberLabel02,R.id.dayNumberLabel03,R.id.dayNumberLabel04,R.id.dayNumberLabel05,R.id.dayNumberLabel06,
		R.id.dayNumberLabel10,R.id.dayNumberLabel11,R.id.dayNumberLabel12,R.id.dayNumberLabel13,R.id.dayNumberLabel14,R.id.dayNumberLabel15,R.id.dayNumberLabel16,
		R.id.dayNumberLabel20,R.id.dayNumberLabel21,R.id.dayNumberLabel22,R.id.dayNumberLabel23,R.id.dayNumberLabel24,R.id.dayNumberLabel25,R.id.dayNumberLabel26,
		R.id.dayNumberLabel30,R.id.dayNumberLabel31,R.id.dayNumberLabel32,R.id.dayNumberLabel33,R.id.dayNumberLabel34,R.id.dayNumberLabel35,R.id.dayNumberLabel36,
		R.id.dayNumberLabel40,R.id.dayNumberLabel41,R.id.dayNumberLabel42,R.id.dayNumberLabel43,R.id.dayNumberLabel44,R.id.dayNumberLabel45,R.id.dayNumberLabel46,
		R.id.dayNumberLabel50,R.id.dayNumberLabel51,R.id.dayNumberLabel52,R.id.dayNumberLabel53,R.id.dayNumberLabel54,R.id.dayNumberLabel55,R.id.dayNumberLabel56
	};
	static private final int[] moonDayViewIds = {
		R.id.moonDayView00,R.id.moonDayView01,R.id.moonDayView02,R.id.moonDayView03,R.id.moonDayView04,R.id.moonDayView05,R.id.moonDayView06,
		R.id.moonDayView10,R.id.moonDayView11,R.id.moonDayView12,R.id.moonDayView13,R.id.moonDayView14,R.id.moonDayView15,R.id.moonDayView16,
		R.id.moonDayView20,R.id.moonDayView21,R.id.moonDayView22,R.id.moonDayView23,R.id.moonDayView24,R.id.moonDayView25,R.id.moonDayView26,
		R.id.moonDayView30,R.id.moonDayView31,R.id.moonDayView32,R.id.moonDayView33,R.id.moonDayView34,R.id.moonDayView35,R.id.moonDayView36,
		R.id.moonDayView40,R.id.moonDayView41,R.id.moonDayView42,R.id.moonDayView43,R.id.moonDayView44,R.id.moonDayView45,R.id.moonDayView46,
		R.id.moonDayView50,R.id.moonDayView51,R.id.moonDayView52,R.id.moonDayView53,R.id.moonDayView54,R.id.moonDayView55,R.id.moonDayView56
	};
	
	private void registerDayLabels() {
		dayLabels=new TextView[dayNumberLabelIds.length];
		for(int i=0; i<dayLabels.length; i++) {
			dayLabels[i]=(TextView) findViewById(dayNumberLabelIds[i]);
		}
	}
	
	private void registerMoonViews() {
		moonViews=new MoonDayView[moonDayViewIds.length];
		for(int i=0; i<moonDayViewIds.length; i++) {
			moonViews[i]=(MoonDayView)findViewById(moonDayViewIds[i]);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registerDayLabels();
		registerMoonViews();
		mPC=new MoonphaseCalculator();
		gestureL=new GestureListener();
		gestureD=new GestureDetector(this, gestureL);
		
		monthYearFormat=getResources().getString(R.string.month_year_format);

		if(savedInstanceState!=null) {
			int lastYear=savedInstanceState.getInt(STATE_YEAR);
			int lastMonth=savedInstanceState.getInt(STATE_MONTH);
			monthShown=new GregorianCalendar(lastYear,lastMonth,1);
		} else {
			setToFirstDayThisMonth();
		}
		refreshCalendar();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(STATE_YEAR, monthShown.get(GregorianCalendar.YEAR));
		savedInstanceState.putInt(STATE_MONTH, monthShown.get(GregorianCalendar.MONTH));
		
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean res=true;
		int itemId=item.getItemId();
		if(itemId == R.id.next_month) {
			monthShown.add(GregorianCalendar.MONTH, 1);
			refreshCalendar();
		} else if (itemId == R.id.prev_month) {
			monthShown.add(GregorianCalendar.MONTH, -1);
			refreshCalendar();
		} else if (itemId == R.id.this_month) {
			setToFirstDayThisMonth();
			refreshCalendar();
		} else if (itemId == R.id.action_legal) {
			LegalDialogFragment dFrag=new LegalDialogFragment();
			dFrag.show(getFragmentManager(), "com.example.mooncal.legaldialog");
		} else {
			res=false;
		}
		return res;
	}

	private void setToFirstDayThisMonth() {
		monthShown=new GregorianCalendar();
		monthShown.set(GregorianCalendar.DAY_OF_MONTH, monthShown.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
	}
	
	public void refreshCalendar() {
		if(monthShown.get(GregorianCalendar.YEAR)<2001) {
			Toast.makeText(this, R.string.before_2001, Toast.LENGTH_SHORT).show();
			monthShown.set(2001, 0, 1);
		}
		
		ActionBar actionBar=getActionBar();
		CharSequence monthyear=DateFormat.format(monthYearFormat, monthShown);
		actionBar.setTitle(monthyear);		
		
		mPC.calc(monthShown.get(GregorianCalendar.YEAR), monthShown.get(GregorianCalendar.MONTH)-GregorianCalendar.JANUARY+1);
		int fstDay=monthShown.getActualMinimum(GregorianCalendar.DAY_OF_MONTH);
		int lstDay=monthShown.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		monthShown.set(GregorianCalendar.DAY_OF_MONTH, fstDay);
		
		GregorianCalendar rightNow = new GregorianCalendar();
		int highlightDay;
		if((rightNow.get(GregorianCalendar.MONTH) == monthShown.get(GregorianCalendar.MONTH)) &&
				(rightNow.get(GregorianCalendar.YEAR) == monthShown.get(GregorianCalendar.YEAR))) {
			highlightDay=rightNow.get(GregorianCalendar.DAY_OF_MONTH);
		} else {
			highlightDay=-1;
		}
		
		int fstDayOfWeek=monthShown.get(GregorianCalendar.DAY_OF_WEEK)-1;
		int fieldnum;
		for(fieldnum=0; fieldnum<fstDayOfWeek; fieldnum++) {
			dayLabels[fieldnum].setVisibility(View.INVISIBLE);
			moonViews[fieldnum].setVisibility(View.INVISIBLE);
		}
		Resources r=getResources();
		int highlightColor=r.getColor(R.color.highlight);
		int normalColor=r.getColor(R.color.ordinaryday);
		for(int day=fstDay; day<=lstDay; day++) {
			int bgcolor=(highlightDay==day)?highlightColor:normalColor;
			dayLabels[fieldnum].setBackgroundColor(bgcolor);
			dayLabels[fieldnum].setText(Integer.toString(day));
			dayLabels[fieldnum].setVisibility(View.VISIBLE);
			moonViews[fieldnum].setBackgroundColor(bgcolor);
			moonViews[fieldnum].setPhaseLunation(mPC.phases[day-1], mPC.lunations[day-1]);
			moonViews[fieldnum].setVisibility(View.VISIBLE);
			fieldnum++;
		}
		for(; fieldnum<dayLabels.length; fieldnum++) {
			dayLabels[fieldnum].setVisibility(View.INVISIBLE);
			moonViews[fieldnum].setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		boolean gest=gestureD.onTouchEvent(e);
		boolean res=false;
		if(e.getAction()==MotionEvent.ACTION_UP) {
			final View bv=findViewById(R.id.baseview);
			int offs=gestureL.getOffset();
			if(Math.abs(offs)>bv.getWidth()/6) {
				int dir=offs>0?-1:1;
				monthShown.add(GregorianCalendar.MONTH, dir);
				refreshCalendar();
				if(anim!=null) { anim.cancel(); }
				int w=bv.getWidth();
				anim=ValueAnimator.ofInt((offs>0?-w:w),0);
				anim.setDuration(100);
				anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						bv.setX((Integer)animation.getAnimatedValue());
					}
				});
				anim.start();
			} else {
				bv.setX(0);
			}
			gestureL.resetOffset();
			res=true;
		}
		return gest|res;
	}
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		private int offset=0;
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			offset=(int) (e2.getX()-e1.getX());
			View bv=MainActivity.this.findViewById(R.id.baseview);
			bv.setX(offset);
			return true; 
			
		}
		public int getOffset() { return offset; }
		public void resetOffset() { offset=0; }
	}
}
