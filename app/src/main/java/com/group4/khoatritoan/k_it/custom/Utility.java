package com.group4.khoatritoan.k_it.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.TimeZone;

public class Utility {

	public static long getCurrentOffsetMilliseconds() {
		long date = System.currentTimeMillis();
		return TimeZone.getDefault().getOffset(date);
	}

	public static long getStartCurrentDateMilliseconds() {
		return getCurrentDateMilliseconds(0, 0, 0, 0);
	}
	public static long getEndCurrentDateMilliseconds() {
		return getCurrentDateMilliseconds(23, 59, 59, 999);
	}

	public static long getCurrentDateMilliseconds(int hour, int minute, int second, int millisecond) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, millisecond);
		return c.getTimeInMillis();
	}

	public static void startAndFinishActivity(Activity activity,  Class<?> className) {
		Intent intent = new Intent(activity, className);
		activity.startActivity(intent);
		activity.finish();
	}

	public static void dismissKeyboard(View view) {

		try {
			Context context = view.getContext();
			InputMethodManager imm = (InputMethodManager)
					context.getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		catch (Exception ignored) {}
		//	Cách khác
		//	InputMethodManager imm = (InputMethodManager)
		//			activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		//	if (null != activity.getCurrentFocus()) {
		//		imm.hideSoftInputFromWindow(
		//				activity.getCurrentFocus()
		//				.getApplicationWindowToken(),
		//				0
		//		);
		//	}
	}
}