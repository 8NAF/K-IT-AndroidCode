package com.group4.khoatritoan.k_it.custom;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.TimeZone;

public class Utility {

	public static long getCurrentOffsetMilliseconds() {
		long date = System.currentTimeMillis();
		return TimeZone.getDefault().getOffset(date);
	}


	public static long getStartCurrentDateMilliseconds() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	public static void dismissKeyboard(Activity activity) {

		try {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (null != activity.getCurrentFocus())
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getApplicationWindowToken(), 0);
		}
		catch (Exception ignored) {}
	}
}
