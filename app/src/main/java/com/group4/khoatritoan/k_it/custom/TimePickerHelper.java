package com.group4.khoatritoan.k_it.custom;

import android.text.format.DateFormat;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;


public class TimePickerHelper {

	public static MaterialTimePicker create(long milliseconds, boolean is24Hour) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		int timeFormat = is24Hour ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;

		MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
				.setHour(calendar.get(Calendar.HOUR_OF_DAY))
				.setMinute(calendar.get(Calendar.MINUTE))
				.setTimeFormat(timeFormat);

		return builder.build();
	}

	public static MaterialTimePicker reBuildTimePicker(MaterialTimePicker timePicker,
			long milliseconds, FragmentActivity activity) {

		boolean is24Hour = DateFormat.is24HourFormat(activity);
		FragmentManager fragmentManager = activity.getSupportFragmentManager();

		MaterialTimePicker newTimePicker = create(milliseconds, is24Hour);

		if (timePicker.isVisible()) {
			timePicker.dismiss();
			timePicker = newTimePicker;
			timePicker.show(fragmentManager, "TIME");
		}
		else {
			timePicker = newTimePicker;
		}

		return timePicker;
	}


}
