package com.group4.khoatritoan.k_it.databinding;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.NumberPicker;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BindingAdapter {

	//#region NumberPicker

	@androidx.databinding.BindingAdapter("android:maxValue")
	public static void setMaxValue(NumberPicker numberPicker, int maxValue) {
		numberPicker.setMaxValue(maxValue);
	}

	@androidx.databinding.BindingAdapter("android:minValue")
	public static void setMinValue(NumberPicker numberPicker, int minValue) {
		numberPicker.setMinValue(minValue);
	}

	//#endregion
	//#region TextInputEditText

	@androidx.databinding.BindingAdapter("android:date")
	public static void setDate(TextInputEditText editText, long milliseconds) {

		Log.e("android:date", milliseconds + "");

		DateFormat formatter = SimpleDateFormat.getDateInstance();
		Date date = new Date(milliseconds);
		String text = formatter.format(date);

		Log.e("android:date", text);
		editText.setText(text);
	}

	@SuppressLint("SimpleDateFormat")
	@androidx.databinding.BindingAdapter("android:timeWithoutSecond")
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

	//#endregion
	//#region MaterialButtonToggleGroup

	@androidx.databinding.BindingAdapter("android:mapButtons")
	public static void setMapButtons(MaterialButtonToggleGroup group, Map<Integer, Boolean> mapsButton) {
		for (Map.Entry<Integer, Boolean> entry : mapsButton.entrySet()) {

			int buttonId = entry.getKey();
			boolean isChecked = entry.getValue();

			if (isChecked) {
				group.check(buttonId);
			}
		}
	}

	@androidx.databinding.BindingAdapter("android:checkedId")
	public static void setCheck(MaterialButtonToggleGroup group, int id) {
		group.check(id);
	}


	@androidx.databinding.BindingAdapter("android:onButtonChecked")
	public static void addOnButtonChecked(MaterialButtonToggleGroup group,
										  MaterialButtonToggleGroup.OnButtonCheckedListener listener) {
		group.addOnButtonCheckedListener(listener);
	}


	//#endregion
	//#region RecyclerView

	@androidx.databinding.BindingAdapter("android:hasFixedSize")
	public static void setHasFixedSize(RecyclerView recyclerView, boolean hasFixedSize) {
		recyclerView.setHasFixedSize(hasFixedSize);
	}

	@androidx.databinding.BindingAdapter("android:layoutManager")
	public static void setLayoutManager(RecyclerView recyclerView, RecyclerView.LayoutManager layout) {
		recyclerView.setLayoutManager(layout);
	}

	@androidx.databinding.BindingAdapter("android:adapter")
	public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
		recyclerView.setAdapter(adapter);
	}

	//#endregion
}