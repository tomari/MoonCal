package com.example.mooncal;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView[] dayLabels;
	private MoonDayView[] moonViews;
	private String monthYearFormat;
	private GregorianCalendar monthShown;
	private MoonphaseCalculator mPC;
	private boolean browsing=false;
	public static final String PREFS_NAME="MoonCalPrefs";
	private static final String PREFS_BROWSING="browsing";
	private static final String PREFS_YEAR="year";
	private static final String PREFS_MONTH="month";
	
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
		
		monthYearFormat=getResources().getString(R.string.month_year_format);
		SharedPreferences settings=getSharedPreferences(PREFS_NAME,0);
		browsing=settings.getBoolean(PREFS_BROWSING, false);
		if(browsing) {
			int lastYear=settings.getInt(PREFS_YEAR, 2013);
			int lastMonth=settings.getInt(PREFS_MONTH, 9);
			monthShown=new GregorianCalendar(lastYear,lastMonth,1);
		} else {
			setToFirstDayThisMonth();
		}
		refreshCalendar();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings=getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor=settings.edit();
		editor.putBoolean(PREFS_BROWSING, browsing);
		editor.putInt(PREFS_YEAR, monthShown.get(Calendar.YEAR));
		editor.putInt(PREFS_MONTH, monthShown.get(Calendar.MONTH));
		editor.commit();
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
			monthShown.add(Calendar.MONTH, 1);
			refreshCalendar();
			browsing=true;
		} else if (itemId == R.id.prev_month) {
			monthShown.add(Calendar.MONTH, -1);
			refreshCalendar();
			browsing=true;
		} else if (itemId == R.id.this_month) {
			setToFirstDayThisMonth();
			browsing=false;
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
		monthShown.set(Calendar.DAY_OF_MONTH, monthShown.getActualMinimum(Calendar.DAY_OF_MONTH));
	}
	
	public void refreshCalendar() {
		if(monthShown.get(Calendar.YEAR)<2001) {
			Toast.makeText(this, R.string.before_2001, Toast.LENGTH_SHORT).show();
			monthShown.set(2001, 0, 1);
		}
		
		ActionBar actionBar=getActionBar();
		CharSequence monthyear=DateFormat.format(monthYearFormat, monthShown);
		actionBar.setTitle(monthyear);
		
		mPC.calc(monthShown.get(Calendar.YEAR), monthShown.get(Calendar.MONTH)-Calendar.JANUARY+1);
		int fstDay=monthShown.getActualMinimum(Calendar.DAY_OF_MONTH);
		int lstDay=monthShown.getActualMaximum(Calendar.DAY_OF_MONTH);
		monthShown.set(Calendar.DAY_OF_MONTH, fstDay);
		int fstDayOfWeek=monthShown.get(Calendar.DAY_OF_WEEK)-1;
		int fieldnum;
		for(fieldnum=0; fieldnum<fstDayOfWeek; fieldnum++) {
			dayLabels[fieldnum].setVisibility(View.INVISIBLE);
			moonViews[fieldnum].setVisibility(View.INVISIBLE);
		}
		for(int day=fstDay; day<=lstDay; day++) {
			dayLabels[fieldnum].setVisibility(View.VISIBLE);
			dayLabels[fieldnum].setText(Integer.toString(day));
			moonViews[fieldnum].setPhaseLunation(mPC.phases[day-1], mPC.lunations[day-1]);
			moonViews[fieldnum].setVisibility(View.VISIBLE);
			fieldnum++;
		}
		for(; fieldnum<dayLabels.length; fieldnum++) {
			dayLabels[fieldnum].setVisibility(View.INVISIBLE);
			moonViews[fieldnum].setVisibility(View.INVISIBLE);
		}
	}
}
