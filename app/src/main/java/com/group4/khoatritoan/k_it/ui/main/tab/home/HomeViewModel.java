package com.group4.khoatritoan.k_it.ui.main.tab.home;

import android.app.AlarmManager;
import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.AlarmManagerHelper;
import com.group4.khoatritoan.k_it.custom.SkippableObserver;
import com.group4.khoatritoan.k_it.model.HomeModel;

import java.util.Calendar;
import java.util.Map;

import static com.group4.khoatritoan.k_it.repository.MyKey.CURRENT_MINUTE;
import static com.group4.khoatritoan.k_it.repository.MyKey.CURRENT_SECOND;
import static com.group4.khoatritoan.k_it.custom.Utility.getCurrentDateMilliseconds;


public class HomeViewModel extends AndroidViewModel {

	//#region Observer
	public final SkippableObserver<Integer> currentMinuteObserver = new SkippableObserver<Integer>() {
		@Override
		public void onChanged(Integer value) {
			if (getIsSkip()) {
				setIsSkip(false);
				return;
			}
			HomeViewModel.this.onCurrentMinuteChange(value);
		}
	};

	public final SkippableObserver<Integer> currentSecondObserver = new SkippableObserver<Integer>() {
		@Override
		public void onChanged(Integer value) {
			if (getIsSkip()) {
				setIsSkip(false);
				return;
			}
			HomeViewModel.this.onCurrentSecondChange(value);
		}
	};

	public final SkippableObserver<Boolean> isTurnOnModeObserver = new SkippableObserver<Boolean>() {
		@Override
		public void onChanged(Boolean value) {
			if (getIsSkip()) {
				setIsSkip(false);
				return;
			}

			HomeViewModel.this.onIsTurnOnModeChange(value);
		}
	};

	//#endregion

	private final HomeModel model;
	private final AlarmManagerHelper alarmManagerHelper;

	public HomeViewModel(Application application) {
		super(application);

		Log.e("Home>HomeViewModel", "init");

		model = new HomeModel(application);
		alarmManagerHelper = new AlarmManagerHelper(application);

		initTurnOnNotificationGroup();
		initDelayNotificationSecondsGroup();
		initAutoModeGroup();
	}

	//#region TurnOnNotificationGroup

	private MutableLiveData<Boolean> turnOnNotification;
	public MutableLiveData<Boolean> getTurnOnNotification() {
		return turnOnNotification;
	}

	private void initTurnOnNotificationGroup() {

		Log.e("Home>HomeViewModel>TONG", "init");
		Log.e("Home>HomeViewModel>TONG", "getTurnOnNotification");
		turnOnNotification = model.getTurnOnNotification();
	}

	public void updateTurnOnNotification(Boolean value) {

		Log.e("Home>HomeViewModel>TONG", "updateTurnOnNotification: " + value);

		model.setTurnOnNotification(value, task -> {
			if (task.isSuccessful()) {
				Log.e("Home>HomeViewModel>TONG", "update TON success: " + value);
			}
			else {
				Log.e("Home>HomeViewModel>TONG", "model TON error: " + task.getException());
			}
		});
	}

	//#endregion

	//#region DelayNotificationSecondsGroup

	private MutableLiveData<Pair<Integer, Integer>> maxTime;
	private MutableLiveData<Pair<Integer, Integer>> minTime;
	private Map<String, MutableLiveData<Integer>> currentTime;
	private MediatorLiveData<Pair<Integer, Integer>> minMaxSecond;

	private MutableLiveData<Integer> visibility1;


	public LiveData<Pair<Integer, Integer>> getMaxTime() {
		return maxTime;
	}
	public LiveData<Pair<Integer, Integer>> getMinTime() {
		return minTime;
	}

	public MutableLiveData<Integer> getCurrentSecond() {
		return currentTime.get(CURRENT_SECOND);
	}
	public MutableLiveData<Integer> getCurrentMinute() {
		return currentTime.get(CURRENT_MINUTE);
	}

	public MutableLiveData<Integer> getVisibility1() { return visibility1; }

	public LiveData<Pair<Integer, Integer>> getMinMaxSecond() {
		return minMaxSecond;
	}


	private void initDelayNotificationSecondsGroup() {

		Log.e("Home>HomeViewModel>DNSG", "init");

		Log.e("Home>HomeViewModel>DNSG", "currentMinuteObserver skip: true");
		Log.e("Home>HomeViewModel>DNSG", "currentSecondObserver skip: true");
		currentMinuteObserver.setIsSkip(true);
		currentSecondObserver.setIsSkip(true);

		maxTime = model.getMaxTime();
		minTime = model.getMinTime();
		currentTime = model.getCurrentTime();
		Log.e("Home>HomeViewModel>DNSG", "init maxTime: " + maxTime.getValue());
		Log.e("Home>HomeViewModel>DNSG", "init minTime: " + minTime.getValue());
		Log.e("Home>HomeViewModel>DNSG", "init currentTime: "
				+ currentTime.get(CURRENT_MINUTE).getValue() + ":"
				+ currentTime.get(CURRENT_SECOND).getValue());

		Log.e("Home>HomeViewModel>DNSG", "init minMaxSecond");
		Log.e("Home>HomeViewModel>DNSG", "minMaxSecond addSource: getCurrentMinute");
		minMaxSecond = new MediatorLiveData<>();
		minMaxSecond.addSource(getCurrentMinute(), this::changeLimitSecond);

		visibility1 = model.getVisibility1();
		Log.e("Home>HomeViewModel>DNSG", "init visibility1: "
			+ (visibility1.getValue() == View.GONE ? "View.GONE" : "View.VISIBLE"));
	}

	private void changeLimitSecond(Integer curMinute) {

		Log.e("Home>HomeViewModel>DNSG", "changeLimitSecond");

		int maxMinute = getMaxTime().getValue().first;
		int minMinute = getMinTime().getValue().first;

		int maxSecond = getMaxTime().getValue().second;
		int minSecond = getMinTime().getValue().second;

		int curSecond = getCurrentSecond().getValue();

		if (maxMinute == minMinute) {
			Log.e("Home>HomeViewModel>DNSG", "maxMinute == minMinute");
			Log.e("Home>HomeViewModel>DNSG", "minMaxSecond: " + minSecond + ", " + maxSecond);
			minMaxSecond.setValue(new Pair<>(minSecond, maxSecond));
			return;
		}

		if (curMinute == maxMinute) {
			Log.e("Home>HomeViewModel>DNSG", "curMinute == maxMinute");
			Log.e("Home>HomeViewModel>DNSG", "minMaxSecond: " + 0 + ", " + maxSecond);
			minMaxSecond.setValue(new Pair<>(0, maxSecond));
			if (curSecond > maxSecond) {
				getCurrentSecond().setValue(maxSecond);
			}
		}
		else if (curMinute == minMinute) {
			Log.e("Home>HomeViewModel>DNSG", "curMinute == minMinute");
			Log.e("Home>HomeViewModel>DNSG", "minMaxSecond: " + minSecond + ", " + 59);
			minMaxSecond.setValue(new Pair<>(minSecond, 59));
			if (curSecond < minSecond) {
				getCurrentSecond().setValue(minSecond);
			}
		}
		else {
			Log.e("Home>HomeViewModel>DNSG", "minMinute < curMinute < minMinute");
			Log.e("Home>HomeViewModel>DNSG", "minMaxSecond: " + 0 + ", " + 59);
			minMaxSecond.setValue(new Pair<>(0, 59));
		}
	}

	private void updateDelayNotificationSeconds() {

		int seconds = getCurrentMinute().getValue() * 60 + getCurrentSecond().getValue();

		Log.e("Home>HomeViewModel>DNSG", "update DNS: " + seconds);
		model.setDelayNotificationSeconds(seconds, task -> {
			if (task.isSuccessful()) {
				Log.e("Home>HomeViewModel>DNSG", "update DNS success: " + seconds);
			}
			else {
				Log.e("Home>HomeViewModel>DNSG", "update DNS error: " + task.getException());
			}
		});
	}

	public void onClickDone1(View view) {
		Log.e("Home>HomeViewModel>DNSG", "onClickDone1");
		updateDelayNotificationSeconds();
		Log.e("Home>HomeViewModel>DNSG", "set visibility1: View.GONE");
		visibility1.setValue(View.GONE);
	}

	public void onClickCancel1(View view) {
		Log.e("Home>HomeViewModel>DNSG", "onClickCancel1");

		Log.e("Home>HomeViewModel>DNSG", "currentMinuteObserver set skip: true");
		Log.e("Home>HomeViewModel>DNSG", "currentSecondObserver set skip: true");
		currentMinuteObserver.setIsSkip(true);
		currentSecondObserver.setIsSkip(true);


		currentTime = model.getCurrentTime();
		visibility1.setValue(View.GONE);
		Log.e("Home>HomeViewModel>DNSG", "reset currentTime: "
				+ getCurrentMinute().getValue() + ":" + getCurrentSecond().getValue());
		Log.e("Home>HomeViewModel>DNSG", "set visibility1: View.GONE");
	}

	private void onCurrentMinuteChange(Integer currentValue) {

		Log.e("Home>HomeViewModel>DNSG", "onCurrentMinuteChange: " + currentValue);
		Log.e("Home>HomeViewModel>DNSG", "set visibility1: View.VISIBLE");
		visibility1.setValue(View.VISIBLE);
	}

	private void onCurrentSecondChange(Integer currentValue) {

		Log.e("Home>HomeViewModel>DNSG", "onCurrentSecondChange: " + currentValue);
		Log.e("Home>HomeViewModel>DNSG", "set visibility1: View.VISIBLE");
		visibility1.setValue(View.VISIBLE);
	}

	//#endregion

	//#region AutoModeGroup

	private MutableLiveData<Boolean> isAutoModeEnabled;
	private MutableLiveData<Boolean> isTurnOnMode;
	private MutableLiveData<Long> startTime;
	private MutableLiveData<Long> endTime;
	private MutableLiveData<Integer> visibility2;

	private void initAutoModeGroup() {

		Log.e("android:binaryCheck", "init");

		isTurnOnModeObserver.setIsSkip(true);

		isAutoModeEnabled = model.getIsAutoModeEnabled();
		isTurnOnMode = model.getIsTurnOnMode();
		startTime = model.getStartTime();
		endTime = model.getEndTime();
		visibility2 = model.getVisibility2();
	}

	public MutableLiveData<Boolean> getIsAutoModeEnabled() {
		return isAutoModeEnabled;
	}
	public MutableLiveData<Boolean> getIsTurnOnMode() {
		return isTurnOnMode;
	}
	public MutableLiveData<Long> getStartTime() {
		return startTime;
	}
	public MutableLiveData<Long> getEndTime() {
		return endTime;
	}
	public MutableLiveData<Integer> getVisibility2() {
		return visibility2;
	}

	public void onClickOkTimePicker(MaterialTimePicker timePicker, MutableLiveData<Long> liveData) {
		liveData.setValue(getCurrentDateMilliseconds(
				timePicker.getHour(), timePicker.getMinute(), 0, 0
		));
		visibility2.setValue(View.VISIBLE);
	}

	public void onIsAutoModeEnabledChange(Boolean enable) {
		if (enable) {
			if (!model.getHasReceiver()) {
				visibility2.setValue(View.VISIBLE);
			}
		}
		else {
			model.setIsAutoModeEnabled(false);
			alarmManagerHelper.cancel();
			visibility2.setValue(View.GONE);
		}
	}

	private void onIsTurnOnModeChange(Boolean isTurnOnMode) {
		visibility2.setValue(View.VISIBLE);
	}

	public void onClickDone2(View v) {

		enforce();
		updateAutoModGroup();
		visibility2.setValue(View.GONE);
	}

	private void enforce() {

		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.set(Calendar.SECOND, 0);
		currentCalendar.set(Calendar.MILLISECOND, 0);
		long currentTime = currentCalendar.getTimeInMillis();
		long startTime = getStartTime().getValue();
		long endTime = getEndTime().getValue();

		Log.e("AlarmManager", "enforce, before start: " + startTime);
		Log.e("AlarmManager", "enforce, before end: " + endTime);
		Log.e("AlarmManager", "enforce, before current: " + currentTime);

		if (startTime <= currentTime) {
			startTime += AlarmManager.INTERVAL_DAY;
		}
		if (endTime <= currentTime) {
			endTime += AlarmManager.INTERVAL_DAY;
		}
		if (endTime <= startTime) {
			endTime += AlarmManager.INTERVAL_DAY;
		}

		if (endTime != getEndTime().getValue()) {
			getEndTime().setValue(endTime);
		}
		if (startTime != getStartTime().getValue()) {
			getStartTime().setValue(startTime);
		}

		Log.e("AlarmManager", "enforce, after start: " + startTime);
		Log.e("AlarmManager", "enforce, after end: " + endTime);
		Log.e("AlarmManager", "enforce, after current: " + currentTime);

		alarmManagerHelper.enforce(startTime, endTime);
	}

	private void updateAutoModGroup() {
		model.setIsAutoModeEnabled(isAutoModeEnabled.getValue());
		model.setIsTurnOnMode(isTurnOnMode.getValue());
		model.setStartTime(startTime.getValue());
		model.setEndTime(endTime.getValue());
	}

	public void onClickCancel2(View v) {

		if (model.getHasReceiver()) {
			initAutoModeGroup();
		}
		else {
			getIsAutoModeEnabled().setValue(false);
			model.setIsAutoModeEnabled(false);
		}

		getVisibility2().setValue(View.GONE);
	}

	public void onIsTurnModeChange(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
		if (isChecked) {
			Boolean oldValue = isTurnOnMode.getValue();
			Boolean newValue = checkedId == R.id.btnTurnOnMode;
			if (oldValue != newValue) {
				getVisibility2().setValue(View.VISIBLE);
				isTurnOnMode.setValue(newValue);
			}
		}
	}

	//#endregion
}