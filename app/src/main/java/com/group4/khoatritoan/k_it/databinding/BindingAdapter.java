package com.group4.khoatritoan.k_it.databinding;

import android.widget.NumberPicker;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

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