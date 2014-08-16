package com.example.mooncal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class AboutDialogPreference extends DialogPreference {
	public AboutDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setupDialog(context);
	}
	public AboutDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDialog(context);
	}
	@Override
	protected void onPrepareDialogBuilder (AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setNegativeButton("", null);
		builder.setTitle("");
	}
	private void setupDialog(Context context) {
		String appname=context.getResources().getString(R.string.app_name);
		String versionName;
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName="";
		}
		final String msg=String.format(context.getResources().getString(R.string.legal_notice),
				appname, versionName);
		setDialogMessage(msg);
	}
}
