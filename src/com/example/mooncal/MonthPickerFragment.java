package com.example.mooncal;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class MonthPickerFragment extends DialogFragment implements OnDateSetListener {
	private OnMonthPickedListener delegate;
	private int month=Calendar.getInstance().get(Calendar.MONTH);
	private int year=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	public OnMonthPickedListener getOnMonthPickedListener() {
		return delegate;
	}
	public void setOnMonthPickedListener(OnMonthPickedListener listener) {
		delegate=listener;
	}
	public void setMonth(int month, int year) {
		this.month=month;
		this.year=year;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog picker=createMonthPickerDialog();
		return picker;
	}

	private DatePickerDialog createMonthPickerDialog() {
		DatePickerDialog dpd=new DatePickerDialog(getActivity(),this,year,month,1);
		DatePicker datePicker=dpd.getDatePicker();
		try {
			Field[] datePickerFields=datePicker.getClass().getDeclaredFields();
			for(Field datePickerField: datePickerFields) {
				if("mDaySpinner".equals(datePickerField.getName())) {
					datePickerField.setAccessible(true);
					View dayPicker=(View) datePickerField.get(datePicker);
					dayPicker.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			return null;
		}
		dpd.setTitle("");
		datePicker.setCalendarViewShown(false);
		return dpd;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		if(null!=delegate) {
			delegate.onMonthPicked(year, monthOfYear);
		}
	}
	public interface OnMonthPickedListener {
		public void onMonthPicked(int year, int monthOfYear);
	}
}
