package com.group4.khoatritoan.k_it.databinding;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;
import android.view.View;

import com.group4.khoatritoan.k_it.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {

	public static int booleanToIdAutoMode(Boolean newValue) {
		return newValue ? R.id.btnTurnOnMode : R.id.btnTurnOffMode;
	}

	@SuppressLint("SimpleDateFormat")
	public static String millisecondToHourMinute(View view, long milliseconds) {

		boolean is24Hour = DateFormat.is24HourFormat(view.getContext());
		SimpleDateFormat formatter = new SimpleDateFormat(is24Hour ? "HH:mm" : "hh:mm a");
		return formatter.format(new Date(milliseconds));
	}

	public static String millisecondToDate(long milliseconds) {

		java.text.DateFormat formatter = SimpleDateFormat.getDateInstance();
		Date date = new Date(milliseconds);
		return formatter.format(date);
	}
}