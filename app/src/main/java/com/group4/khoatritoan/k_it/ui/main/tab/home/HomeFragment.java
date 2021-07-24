package com.group4.khoatritoan.k_it.ui.main.tab.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.databinding.FragmentHomeBinding;
import com.group4.khoatritoan.k_it.repository.HomeRepository;
import com.group4.khoatritoan.k_it.service.EndTimeReceiver;
import com.group4.khoatritoan.k_it.service.StartTimeReceiver;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;


import static com.group4.khoatritoan.k_it.repository.DatabasePath.TURN_ON_NOTIFICATION_PATH;

public class HomeFragment extends Fragment {

	private HomeViewModel homeViewModel;
	private FragmentHomeBinding binding;
	private MaterialTimePicker startTimePicker;
	private MaterialTimePicker endTimePicker;
	private boolean is24Hour;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentHomeBinding.inflate(inflater, container, false);
		binding.setLifecycleOwner(getViewLifecycleOwner());

		homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
		binding.setViewModel(homeViewModel);

		//region CardView1

		homeViewModel.initTurnOnNotificationGroup(getContext());
		homeViewModel.getIsTurnOnNotification().observe(getViewLifecycleOwner(), homeViewModel::updateTurnOnNotificationToFirebase);
		//endregion
		//region CardView2

		// Max và Min của trường giây sẽ thay đổi theo số phút được chọn
		homeViewModel.getCurrentMinute().observe(getViewLifecycleOwner(), this::changeLimitSeconds);

		FloatingActionButton fabDone1 = binding.fabDone1;
		FloatingActionButton fabCancel1 = binding.fabCancel1;

		NumberPicker.OnScrollListener changeVisibility = (view, scrollState) -> {
			fabDone1.setVisibility(View.VISIBLE);
			fabCancel1.setVisibility(View.VISIBLE);
		};
		binding.npMinute.setOnScrollListener(changeVisibility);
		binding.npSecond.setOnScrollListener(changeVisibility);

		fabCancel1.setOnClickListener(v -> {
			homeViewModel.resetCurrentTime();
			fabDone1.setVisibility(View.GONE);
			fabCancel1.setVisibility(View.GONE);
		});

		fabDone1.setOnClickListener(v -> {
			homeViewModel.updateDelayNotificationSecondsToFirebase();
			fabDone1.setVisibility(View.GONE);
			fabCancel1.setVisibility(View.GONE);
		});

		//endregion
		//region CardView 3

		homeViewModel.initAutoModeGroup(getContext());

		//autoModeIsEnabled
		FloatingActionButton fabDone2 = binding.fabDone2;
		FloatingActionButton fabCancel2 = binding.fabCancel2;

		homeViewModel.getAutoModeIsEnabled().observe(getViewLifecycleOwner(), enable -> {

			if (enable) {
				if (! homeViewModel.hasReceiver(getContext())) {
					fabDone2.setVisibility(View.VISIBLE);
					fabCancel2.setVisibility(View.VISIBLE);
				}
			}
			else {

				homeViewModel.updateAutoModeIsEnabledToRepository(getContext(), false);
				cancelAlertManager(StartTimeReceiver.class);
				cancelAlertManager(EndTimeReceiver.class);

				fabDone2.setVisibility(View.GONE);
				fabCancel2.setVisibility(View.GONE);
			}
		});

		//isTurnOnMode
		binding.tgAutoMode.check(homeViewModel.getIsTurnOnMode().getValue() ? R.id.btnTurnOnMode : R.id.btnTurnOffMode);
		binding.tgAutoMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
			if (isChecked) {
				homeViewModel.getIsTurnOnMode().setValue(checkedId == R.id.btnTurnOnMode);
				fabDone2.setVisibility(View.VISIBLE);
				fabCancel2.setVisibility(View.VISIBLE);
			}
		});

		//time picker
		is24Hour = DateFormat.is24HourFormat(getActivity());

		startTimePicker = createMaterialTimePicker(homeViewModel.getStartTime().getValue(), is24Hour);
		binding.btnStartTime.setOnClickListener(v -> {
			startTimePicker.show(getActivity().getSupportFragmentManager(), "TIME");
		});
		startTimePicker.addOnPositiveButtonClickListener(v -> {
			homeViewModel.getStartTime().setValue(homeViewModel.getStringFromTime(
					startTimePicker.getHour(),
					startTimePicker.getMinute()
			));
			fabDone2.setVisibility(View.VISIBLE);
			fabCancel2.setVisibility(View.VISIBLE);
		});

		endTimePicker = createMaterialTimePicker(homeViewModel.getEndTime().getValue(), is24Hour);
		binding.btnEndTime.setOnClickListener(v -> {
			endTimePicker.show(getActivity().getSupportFragmentManager(), "TIME");
		});
		endTimePicker.addOnPositiveButtonClickListener(v -> {
			homeViewModel.getEndTime().setValue(homeViewModel.getStringFromTime(
					endTimePicker.getHour(),
					endTimePicker.getMinute()
			));
			fabDone2.setVisibility(View.VISIBLE);
			fabCancel2.setVisibility(View.VISIBLE);
		});

		fabDone2.setOnClickListener(v ->  {
			startAlarmManager();
			homeViewModel.updateAutoModeIsEnabledToRepository(getContext(), homeViewModel.getAutoModeIsEnabled().getValue());
			homeViewModel.updateIsTurnOnModeToRepository(getContext(), homeViewModel.getIsTurnOnMode().getValue());
			homeViewModel.updateStartTimeToRepository(getContext(), homeViewModel.getStartTime().getValue());
			homeViewModel.updateEndTimeToRepository(getContext(), homeViewModel.getEndTime().getValue());
			fabDone2.setVisibility(View.GONE);
			fabCancel2.setVisibility(View.GONE);
		});
		fabCancel2.setOnClickListener(v -> {
			if (homeViewModel.hasReceiver(getContext())) {
				homeViewModel.initAutoModeGroup(getContext());
				binding.tgAutoMode.check(homeViewModel.getIsTurnOnMode().getValue() ? R.id.btnTurnOnMode : R.id.btnTurnOffMode);
			}
			else {
				homeViewModel.getAutoModeIsEnabled().setValue(false);
				homeViewModel.updateAutoModeIsEnabledToRepository(getContext(), false);
			}

			fabDone2.setVisibility(View.GONE);
			fabCancel2.setVisibility(View.GONE);
		});

		//endregion

		return binding.getRoot();
	}

	private void cancelAlertManager(Class<? extends BroadcastReceiver> className) {

		Log.e("cancelAlertManager", className.getName());

		Intent intent = new Intent(getActivity(), className);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

		homeViewModel.setHasReceiver(getContext(), false);
	}


	private void startAlarmManager() {

		Calendar startCalendar = getCalendar(homeViewModel.getStartTime());
		Calendar endCalendar = getCalendar(homeViewModel.getEndTime());

		Log.e("startAlarmManager", "b start: " + startCalendar.getTimeInMillis());
		Log.e("startAlarmManager", "b end: " + endCalendar.getTimeInMillis());

		if (startCalendar.after(endCalendar) || startCalendar.equals(endCalendar)) {
			endCalendar.add(Calendar.DATE, 1);
		}

		Log.e("startAlarmManager", "a start: " + startCalendar.getTimeInMillis());
		Log.e("startAlarmManager", "a end: " + endCalendar.getTimeInMillis());

		PendingIntent startPI = getPendingIntent(StartTimeReceiver.class,
				startCalendar.getTimeInMillis());
		PendingIntent endPI = getPendingIntent(EndTimeReceiver.class,
				endCalendar.getTimeInMillis());

		AlarmManager alarmManager = (AlarmManager) getActivity()
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
				startCalendar.getTimeInMillis(), startPI);

		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
				endCalendar.getTimeInMillis(), endPI);

//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startCalendar.getTimeInMillis(),
//				AlarmManager.INTERVAL_DAY, startPI);
//
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, endCalendar.getTimeInMillis(),
//				AlarmManager.INTERVAL_DAY, endPI);

		homeViewModel.setHasReceiver(getContext(), true);
	}

	private Calendar getCalendar(MutableLiveData<String> timeLiveData) {

		String time = timeLiveData.getValue();
		int hour =  Integer.parseInt(time.substring(0, 2));
		int minute = Integer.parseInt(time.substring(3, 5));

		Log.e("startAlarmManager", hour + ":" + minute);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	private PendingIntent getPendingIntent(Class<? extends BroadcastReceiver> className,
										   long triggerMilliseconds) {
		Intent intent = new Intent(getActivity(), className);
		intent.putExtra("triggerMilliseconds", triggerMilliseconds);

		return PendingIntent.getBroadcast(getActivity(), 1,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	@Override
	public void onResume() {
		super.onResume();

		boolean newIs24Hour = DateFormat.is24HourFormat(getActivity());
		if (is24Hour != newIs24Hour) {
			is24Hour = newIs24Hour;

			startTimePicker= reBuildTimePicker(startTimePicker);
			endTimePicker = reBuildTimePicker(endTimePicker);

			homeViewModel.getStartTime().setValue(homeViewModel.getStartTime().getValue());
			homeViewModel.getEndTime().setValue(homeViewModel.getEndTime().getValue());
		}


	}

	private MaterialTimePicker reBuildTimePicker(MaterialTimePicker timePicker) {

		boolean newIs24Hour = DateFormat.is24HourFormat(getActivity());
		if (is24Hour != newIs24Hour) {

			is24Hour = newIs24Hour;
			MaterialTimePicker newTimePicker = createMaterialTimePicker(
					homeViewModel.getStringFromTime(timePicker.getHour(), timePicker.getMinute()),
					is24Hour
			);

			if (timePicker.isVisible()) {
				timePicker.dismiss();
				timePicker = newTimePicker;
				timePicker.show(getActivity().getSupportFragmentManager(), "TIME");
			}
			else {
				timePicker = newTimePicker;
			}
		}

		return timePicker;
	}

	private MaterialTimePicker createMaterialTimePicker(String timeWithoutSecond, boolean is24Hour) {

		Pair<Integer, Integer> time = homeViewModel.getTimeToString(timeWithoutSecond);

		int timeFormat = is24Hour ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;
		MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
				.setTimeFormat(timeFormat)
				.setHour(time.first)
				.setMinute(time.second);

		return builder.build();
	};


	private void changeLimitSeconds(Integer curMinute) {

		NumberPicker npSecond = binding.npSecond;

		int maxMinute = homeViewModel.getMaxTime().getValue().first;
		int minMinute = homeViewModel.getMinTime().getValue().first;

		int maxSecond = homeViewModel.getMaxTime().getValue().second;
		int minSecond = homeViewModel.getMinTime().getValue().second;

		int curSecond = homeViewModel.getCurrentSecond().getValue();

		if (maxMinute == minMinute) {
			npSecond.setMaxValue(maxSecond);
			npSecond.setMinValue(minSecond);
			return;
		}

		if (curMinute == maxMinute) {
			npSecond.setMaxValue(maxSecond);
			npSecond.setMinValue(0);
			homeViewModel.getCurrentSecond().setValue(Math.min(curSecond, maxSecond));
		}
		else if (curMinute == minMinute) {
			npSecond.setMaxValue(59);
			npSecond.setMinValue(minSecond);
			homeViewModel.getCurrentSecond().setValue(Math.max(curSecond, minSecond));
		}
		else {
			npSecond.setMaxValue(59);
			npSecond.setMinValue(0);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
		HomeRepository.getInstance().setTurnOnNotification(getContext(),
				homeViewModel.getIsTurnOnNotification().getValue());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

}