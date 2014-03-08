package com.example.mooncal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;

public class LegalDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Activity a=getActivity();
		AlertDialog.Builder builder=new AlertDialog.Builder(a);
		String versionName;
		try {
			versionName = a.getPackageManager().getPackageInfo(a.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName="";
		}
		final String msg=String.format(a.getResources().getString(R.string.legal_notice),
				versionName);
		builder.setMessage(msg)
				.setPositiveButton(R.string.dismiss, null)
				.setNeutralButton(R.string.legal_goweb, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String url=a.getResources().getString(R.string.legal_url);
						Intent intent=new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
				});
		return builder.create();
	}
}
