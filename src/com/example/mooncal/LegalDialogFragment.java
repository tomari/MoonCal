package com.example.mooncal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class LegalDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.legal_notice)
				.setPositiveButton(R.string.dismiss, null);
		return builder.create();
	}
}
