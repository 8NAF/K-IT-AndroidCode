package com.group4.khoatritoan.k_it.custom;

import android.text.format.DateFormat;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import static com.group4.khoatritoan.k_it.custom.Utility.getTimeToString;

public class TimePickerHelper {

	public static MaterialTimePicker create(String timeWithoutSecond, boolean is24Hour) {
		Pair<Integer, Integer> time = getTimeToString(timeWithoutSecond);

		int timeFormat = is24Hour ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;
		MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
				.setHour(time.first)
				.setMinute(time.second)
				.setTimeFormat(timeFormat);

		return builder.build();
	}

	public static MaterialTimePicker reBuildTimePicker(MaterialTimePicker timePicker,
			String timeWithoutSecond, FragmentActivity activity) {

		boolean is24Hour = DateFormat.is24HourFormat(activity);
		FragmentManager fragmentManager = activity.getSupportFragmentManager();

		MaterialTimePicker newTimePicker = TimePickerHelper.create(
				timeWithoutSecond,
				is24Hour
		);

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
