package com.example.mooncal;

import java.util.GregorianCalendar;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView[] dayLabels;
	private MoonDayView[] moonViews;
	private GregorianCalendar monthShown;
	private MoonphaseCalculator mPC;
	private static final String STATE_YEAR="year";
	private static final String STATE_MONTH="month";
	private GestureDetector gestureD;
	private GestureListener gestureL;
	private ValueAnimator anim=null;
	private int DoWoffset=0;
	
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
	private void rotateMoonViews() {
		SharedPreferences shrP=PreferenceManager.getDefaultSharedPreferences(this);
		boolean southhemi=shrP.getBoolean(SettingsActivity.SOUTHHEMI, false);
		for(MoonDayView v: moonViews) {
			v.setRotation(southhemi?180:0);
		}
	}
	private void rotateDoWlabels() {
		final int[] DoWlabelId={
			R.id.sundayLabel, R.id.mondayLabel, R.id.tuesdayLabel, R.id.wednesdayLabel,
			R.id.thursdayLabel, R.id.fridayLabel, R.id.saturdayLabel };
		final int[] DoWtxtId={
			R.string.sunday_label, R.string.monday_label, R.string.tuesday_label, R.string.wednesday_label,
			R.string.thursday_label, R.string.friday_label, R.string.saturday_label };
		SharedPreferences shrP=PreferenceManager.getDefaultSharedPreferences(this);
		DoWoffset=shrP.getBoolean(SettingsActivity.WEEKDAY1,false)?1:0;
		for(int i=0; i<DoWlabelId.length; i++) {
			TextView v=(TextView) findViewById(DoWlabelId[i]);
			v.setText(DoWtxtId[(i+DoWoffset)%DoWtxtId.length]);
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
		
		if(savedInstanceState!=null) {
			int lastYear=savedInstanceState.getInt(STATE_YEAR);
			int lastMonth=savedInstanceState.getInt(STATE_MONTH);
			monthShown=new GregorianCalendar(lastYear,lastMonth,1);
		} else {
			setToFirstDayThisMonth();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		rotateDoWlabels();
		rotateMoonViews();
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
		} else if(itemId==R.id.action_settings) {
			Intent intent=new Intent(this,SettingsActivity.class);
			startActivity(intent);
		} else {
			res=false;
		}
		return res;
	}

	private void setToFirstDayThisMonth() {
		monthShown=new GregorianCalendar();
		monthShown.set(GregorianCalendar.DAY_OF_MONTH, monthShown.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
	}
	
	private void updateActionBarTitle(GregorianCalendar month) {
		String monthYearFormat=getResources().getString(R.string.month_year_format);
		ActionBar actionBar=getActionBar();
		CharSequence monthyear=DateFormat.format(monthYearFormat, month);
		actionBar.setTitle(monthyear);		
	}
	
	private int calcHighlightDay(GregorianCalendar month) {
		int highlightDay;
		GregorianCalendar rightNow = new GregorianCalendar();
		if((rightNow.get(GregorianCalendar.MONTH) == monthShown.get(GregorianCalendar.MONTH)) &&
				(rightNow.get(GregorianCalendar.YEAR) == monthShown.get(GregorianCalendar.YEAR))) {
			highlightDay=rightNow.get(GregorianCalendar.DAY_OF_MONTH);
		} else {
			highlightDay=-1;
		}
		return highlightDay;
	}
	
	public void refreshCalendar() {
		if(monthShown.get(GregorianCalendar.YEAR)<2001) {
			Toast.makeText(this, R.string.before_2001, Toast.LENGTH_SHORT).show();
			monthShown.set(2001, 0, 1);
		}
		updateActionBarTitle(monthShown);
		
		mPC.calc(monthShown.get(GregorianCalendar.YEAR), monthShown.get(GregorianCalendar.MONTH)-GregorianCalendar.JANUARY+1);
		int fstDay=monthShown.getActualMinimum(GregorianCalendar.DAY_OF_MONTH);
		int lstDay=monthShown.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		monthShown.set(GregorianCalendar.DAY_OF_MONTH, fstDay);
		
		int fstDayOfWeek=(monthShown.get(GregorianCalendar.DAY_OF_WEEK)-1-DoWoffset)%7;
		int fieldnum;
		for(fieldnum=0; fieldnum<fstDayOfWeek; fieldnum++) {
			dayLabels[fieldnum].setVisibility(View.INVISIBLE);
			moonViews[fieldnum].setVisibility(View.INVISIBLE);
		}
		Resources r=getResources();
		int highlightColor=r.getColor(R.color.highlight);
		int normalColor=r.getColor(R.color.ordinaryday);
		int highlightDay=calcHighlightDay(monthShown);
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
			int thresh=getResources().getDimensionPixelSize(R.dimen.scroll_thresh);
			if(Math.abs(offs)>thresh) {
				int dir=offs>0?-1:1;
				monthShown.add(GregorianCalendar.MONTH, dir);
				refreshCalendar();
				if(anim!=null) {
					anim.cancel();
				} else {
					anim=new ValueAnimator();
					anim.setDuration(100);
					anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							bv.setX((Integer)animation.getAnimatedValue());
						}
					});
				} 
				int w=bv.getWidth();
				anim.setIntValues((offs>0?-w:w),0);
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
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT) {
			monthShown.add(GregorianCalendar.MONTH, -1);
		} else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
			monthShown.add(GregorianCalendar.MONTH, 1);
		} else if(keyCode==KeyEvent.KEYCODE_BUTTON_Y) {
			setToFirstDayThisMonth();
		} else {
			return super.onKeyDown(keyCode, event);
		}
		refreshCalendar();
		return true;
	}
}
