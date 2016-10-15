package com.example.mooncal;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

public class SettingsActivity extends Activity {
	public static final String SOUTHHEMI="pref_south_hemi";
	public static final String WEEKDAY1="pref_weekstart1";
	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.prefs);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
		try {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		} catch (NullPointerException ignored) {};
	}
	public boolean onMenuItemSelected(int featureId,MenuItem item) {
		int itemid=item.getItemId();
		if(itemid==android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
