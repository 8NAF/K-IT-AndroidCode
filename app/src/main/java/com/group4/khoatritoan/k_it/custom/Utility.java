package com.group4.khoatritoan.k_it.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.util.Pair;

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

	public static void startAndFinishActivity(Activity activity,  Class<?> className) {
		Intent intent = new Intent(activity, className);
		activity.startActivity(intent);
		activity.finish();
	}

	public static Pair<Integer, Integer> getTimeToString(String timeWithoutSecond) {

		int hour = Integer.parseInt(timeWithoutSecond.substring(0, 2));
		int minute = Integer.parseInt(timeWithoutSecond.substring(3, 5));

		return new Pair<>(hour, minute);
	}

	public static String getStringFromTime(int hour, int minute) {
		String result = "";

		//HH:mm
		result += (hour < 10 ? "0" : "") + hour
				+ ":" +
				(minute < 10 ? "0" : "") + minute;
		return result;
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


