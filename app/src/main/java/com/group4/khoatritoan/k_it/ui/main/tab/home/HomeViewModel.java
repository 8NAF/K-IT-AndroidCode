package com.group4.khoatritoan.k_it.ui.main.tab.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.khoatritoan.k_it.repository.HomeRepository;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.DELAY_NOTIFICATION_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.TURN_ON_NOTIFICATION_PATH;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.AUTO_MODE_GROUP;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.AUTO_MODE_IS_ENABLED;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.CURRENT_MINUTE;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.CURRENT_SECOND;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.END_TIME;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.IS_TURN_ON_MODE;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.START_TIME;

public class HomeViewModel extends ViewModel {

	private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
	private static final HomeRepository repository = HomeRepository.getInstance();

	private MutableLiveData<Boolean> isTurnOnNotification;

	public void initTurnOnNotificationGroup(Context context) {
		isTurnOnNotification = repository.getIsTurnOnNotification(context);
	}

	private MutableLiveData<Pair<Integer, Integer>> maxTime;
	private MutableLiveData<Pair<Integer, Integer>> minTime;
	private Map<String, MutableLiveData<Integer>> currentTime;


	public HomeViewModel() {

//		isTurnOnNotification = repository.getIsTurnOnNotification();

		maxTime = repository.getMaxTime();
		minTime = repository.getMinTime();
		currentTime = repository.getCurrentTime();
	}

	private MutableLiveData<Boolean> autoModeIsEnabled;
	private MutableLiveData<Boolean> isTurnOnMode;
	private MutableLiveData<String> startTime;
	private MutableLiveData<String> endTime;

	public void initAutoModeGroup(Context context) {
		autoModeIsEnabled = repository.getAutoModeIsEnabled(context);
		isTurnOnMode = repository.getIsTurnOnMode(context);
		startTime = repository.getStartTime(context);
		endTime = repository.getEndTime(context);
	}

	public MutableLiveData<Boolean> getAutoModeIsEnabled() {
		return autoModeIsEnabled;
	}
	public MutableLiveData<Boolean> getIsTurnOnMode() {
		return isTurnOnMode;
	}
	public MutableLiveData<String> getStartTime() {
		return startTime;
	}
	public MutableLiveData<String> getEndTime() {
		return endTime;
	}


	public MutableLiveData<Boolean> getIsTurnOnNotification() {
		return isTurnOnNotification;
	}

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

	public void resetCurrentTime() {
		currentTime = repository.getCurrentTime();
	}

	public void updateTurnOnNotificationToFirebase(Boolean value) {

		DatabaseReference ref = database.getReference(TURN_ON_NOTIFICATION_PATH);
		ref.setValue(value).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Log.e("model TON success", value + "");
			}
			else {
				Log.e("model TON error", task.getException() + "");
			}
		});
	}

	public void updateDelayNotificationSecondsToFirebase() {

		int seconds = getCurrentMinute().getValue() * 60 + getCurrentSecond().getValue();

		Log.e("update DNS", seconds + "");

		DatabaseReference ref = database.getReference(DELAY_NOTIFICATION_SECONDS_PATH);
		ref.setValue(seconds).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Log.e("update DNS success", seconds + "");
			}
			else {
				Log.e("update DNS error", task.getException().getMessage());
			}
		});
	}

	public String getStringFromTime(int hour, int minute) {
		String result = "";

		//HH:mm
		result += (hour < 10 ? "0" : "") + hour
				+ ":" +
				(minute < 10 ? "0" : "") + minute;
		return result;
	}

	public Pair<Integer, Integer> getTimeToString(String timeWithoutSecond) {

		int hour = Integer.parseInt(timeWithoutSecond.substring(0, 2));
		int minute = Integer.parseInt(timeWithoutSecond.substring(3, 5));

		return new Pair<>(hour, minute);
	}

	public void updateAutoModeIsEnabledToRepository(Context context, Boolean value) {
		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		sp.edit().putBoolean(AUTO_MODE_IS_ENABLED, value).apply();
	}

	public void updateIsTurnOnModeToRepository(Context context, Boolean value) {
		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		sp.edit().putBoolean(IS_TURN_ON_MODE, value).apply();
	}

	public void updateStartTimeToRepository(Context context, String value) {
		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		sp.edit().putString(START_TIME, value).apply();
	}

	public void updateEndTimeToRepository(Context context, String value) {
		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		sp.edit().putString(END_TIME, value).apply();
	}

	public boolean hasReceiver(Context context) {
		return repository.hasReceiver(context);
	}

	public void setHasReceiver(Context context, boolean value) {
		repository.setHasReceiver(context, value);
	}
}