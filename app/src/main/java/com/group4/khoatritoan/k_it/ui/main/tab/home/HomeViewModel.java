package com.group4.khoatritoan.k_it.ui.main.tab.home;

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

import java.util.Map;

import static com.group4.khoatritoan.k_it.custom.MyKey.CURRENT_MINUTE;
import static com.group4.khoatritoan.k_it.custom.MyKey.CURRENT_SECOND;
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

			Log.e("android:binaryCheck", "skip: false, isTurnOnMode-in: " + value);
			Log.e("android:binaryCheck", "skip: false, isTurnOnMode-out: " + isTurnOnMode.getValue());
			HomeViewModel.this.onIsTurnOnModeChange(value);
		}
	};

	//#endregion

	private final HomeModel model;
	private final AlarmManagerHelper alarmManagerHelper;

	public HomeViewModel(Application application) {
		super(application);
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
		turnOnNotification = model.getTurnOnNotification();
	}

	public void updateTurnOnNotification(Boolean value) {

		model.setTurnOnNotification(value, task -> {
			if (task.isSuccessful()) {
				Log.e("model TON success", value + "");
			}
			else {
				Log.e("model TON error", task.getException() + "");
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
		currentMinuteObserver.setIsSkip(true);
		currentSecondObserver.setIsSkip(true);

		maxTime = model.getMaxTime();
		minTime = model.getMinTime();
		currentTime = model.getCurrentTime();

		minMaxSecond = new MediatorLiveData<>();
		minMaxSecond.addSource(getCurrentMinute(), this::changeLimitSecond);

		visibility1 = model.getVisibility1();
	}

	private void changeLimitSecond(Integer curMinute) {
		int maxMinute = getMaxTime().getValue().first;
		int minMinute = getMinTime().getValue().first;

		int maxSecond = getMaxTime().getValue().second;
		int minSecond = getMinTime().getValue().second;

		int curSecond = getCurrentSecond().getValue();

		if (maxMinute == minMinute) {
			minMaxSecond.setValue(new Pair<>(minSecond, maxSecond));
			return;
		}

		if (curMinute == maxMinute) {
			minMaxSecond.setValue(new Pair<>(0, maxSecond));
			getCurrentSecond().setValue(Math.min(curSecond, maxSecond));
		}
		else if (curMinute == minMinute) {
			minMaxSecond.setValue(new Pair<>(minSecond, 59));
			getCurrentSecond().setValue(Math.max(curSecond, minSecond));
		}
		else {
			minMaxSecond.setValue(new Pair<>(0, 59));
		}
	}

	private void updateDelayNotificationSeconds() {

		int seconds = getCurrentMinute().getValue() * 60 + getCurrentSecond().getValue();

		Log.e("update DNS", seconds + "");
		model.setDelayNotificationSeconds(seconds, task -> {
			if (task.isSuccessful()) {
				Log.e("update DNS success", seconds + "");
			}
			else {
				Log.e("update DNS error", task.getException().getMessage());
			}
		});
	}

	public void onClickDone1(View view) {
		updateDelayNotificationSeconds();
		visibility1.setValue(View.GONE);
	}

	public void onClickCancel1(View view) {
		currentMinuteObserver.setIsSkip(true);
		currentSecondObserver.setIsSkip(true);

		currentTime = model.getCurrentTime();
		visibility1.setValue(View.GONE);
	}

	private void onCurrentMinuteChange(Integer currentValue) {

		visibility1.setValue(View.VISIBLE);
		Log.e("onCurrentTimeChange", currentValue + "");
	}

	private void onCurrentSecondChange(Integer currentValue) {

		visibility1.setValue(View.VISIBLE);
		Log.e("onCurrentTimeChange", currentValue + "");
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
				timePicker.getHour(),
				timePicker.getMinute()
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
			model.setHasReceiver(false);
			alarmManagerHelper.cancel();
			visibility2.setValue(View.GONE);
		}
	}

	private void onIsTurnOnModeChange(Boolean isTurnOnMode) {
		visibility2.setValue(View.VISIBLE);
	}

	public void onClickDone2(View v) {

		alarmManagerHelper.enforce(startTime.getValue(), endTime.getValue());
		updateAutoModeGroup();
		model.setHasReceiver(true);
		visibility2.setValue(View.GONE);
	}
	private void updateAutoModeGroup() {
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