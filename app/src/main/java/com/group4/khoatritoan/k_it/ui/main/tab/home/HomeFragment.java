package com.group4.khoatritoan.k_it.ui.main.tab.home;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.timepicker.MaterialTimePicker;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.TimePickerHelper;
import com.group4.khoatritoan.k_it.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {

	private HomeViewModel viewModel;
	private FragmentHomeBinding binding;

	private MaterialTimePicker startTimePicker;
	private MaterialTimePicker endTimePicker;
	private boolean is24Hour;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		Log.e("Home>HomeFragment", "onCreateView");

		binding = FragmentHomeBinding.inflate(inflater, container, false);
		binding.setLifecycleOwner(getViewLifecycleOwner());

		viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
		binding.setViewModel(viewModel);

		//CardView 1

		viewModel.getTurnOnNotification().observe(getViewLifecycleOwner(), viewModel::updateTurnOnNotification);

		//CardView 2

		viewModel.getCurrentMinute().observe(getViewLifecycleOwner(), viewModel.currentMinuteObserver);
		viewModel.getCurrentSecond().observe(getViewLifecycleOwner(), viewModel.currentSecondObserver);

		//CardView3

		viewModel.getIsAutoModeEnabled().observe(getViewLifecycleOwner(),
				viewModel::onIsAutoModeEnabledChange);

		Boolean isTurnOnMode = viewModel.getIsTurnOnMode().getValue();
		binding.tgAutoMode.check(isTurnOnMode ? R.id.btnTurnOnMode : R.id.btnTurnOffMode);
		viewModel.getIsTurnOnMode().observe(getViewLifecycleOwner(), viewModel.isTurnOnModeObserver);

		setUpTimePickers();
		startTimePicker.addOnPositiveButtonClickListener(v ->
			viewModel.onClickOkTimePicker(startTimePicker, viewModel.getStartTime())
		);
		endTimePicker.addOnPositiveButtonClickListener(v ->
				viewModel.onClickOkTimePicker(endTimePicker, viewModel.getEndTime())
		);

		return binding.getRoot();
	}

	private void setUpTimePickers() {
		is24Hour = DateFormat.is24HourFormat(getContext());

		startTimePicker = TimePickerHelper.create(viewModel.getStartTime().getValue(), is24Hour);
		binding.btnStartTime.setOnClickListener(v ->
				startTimePicker.show(getChildFragmentManager(), "TIME")
		);

		endTimePicker = TimePickerHelper.create(viewModel.getEndTime().getValue(), is24Hour);
		binding.btnEndTime.setOnClickListener(v -> {
			endTimePicker.show(getChildFragmentManager(), "TIME");
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.e("Home>HomeFragment", "onResume");

		boolean newIs24Hour = DateFormat.is24HourFormat(getContext());
		if (is24Hour != newIs24Hour) {
			is24Hour = newIs24Hour;

			Long startTime = viewModel.getStartTime().getValue();
			Long endTime = viewModel.getEndTime().getValue();

			startTimePicker = TimePickerHelper.reBuildTimePicker(startTimePicker, startTime, getActivity());
			endTimePicker = TimePickerHelper.reBuildTimePicker(endTimePicker, endTime, getActivity());

			startTimePicker.addOnPositiveButtonClickListener(v ->
					viewModel.onClickOkTimePicker(startTimePicker, viewModel.getStartTime())
			);
			endTimePicker.addOnPositiveButtonClickListener(v ->
					viewModel.onClickOkTimePicker(endTimePicker, viewModel.getEndTime())
			);

			viewModel.getStartTime().setValue(startTime);
			viewModel.getEndTime().setValue(endTime);
		}
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
		Log.e("Home>HomeFragment", "onDestroyView");
	}
}