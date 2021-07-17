package com.group4.khoatritoan.k_it.custom;

import android.util.Log;
import android.widget.NumberPicker;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyBindingAdapter {

	@BindingAdapter("android:maxValue")
	public static void setMaxValue(NumberPicker numberPicker, int maxValue) {
		numberPicker.setMaxValue(maxValue);
	}

	@BindingAdapter("android:minValue")
	public static void setMinValue(NumberPicker numberPicker, int minValue) {
		numberPicker.setMinValue(minValue);
	}

	@BindingAdapter("android:date")
	public static void setDate(TextInputEditText editText, long milliseconds) {

		Log.e("android:date", milliseconds + "");

		DateFormat formatter = SimpleDateFormat.getDateInstance();
		Date date = new Date(milliseconds);
		String text = formatter.format(date);

		Log.e("android:date", text);
		editText.setText(text);
	}

	@BindingAdapter("android:timeWithoutSecond")
	public static void setTimeWithoutSecond(TextInputEditText editText, String time24WithoutSecond) {

		boolean is24Hour = android.text.format.DateFormat.is24HourFormat(editText.getContext());
		if (is24Hour) {
			editText.setText(time24WithoutSecond);
			return;
		}

		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
			final Date dateObj = sdf.parse(time24WithoutSecond);
			editText.setText(new SimpleDateFormat("hh:mm a").format(dateObj));
		}
		catch (Exception e) {
			editText.setText("Parse Error");
		}
	}
}
