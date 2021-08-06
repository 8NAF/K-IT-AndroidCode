package com.group4.khoatritoan.k_it.model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group4.khoatritoan.k_it.repository.LocalRepository;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.group4.khoatritoan.k_it.repository.DatabasePath.DELAY_NOTIFICATION_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.MAX_DELAY_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.MIN_DELAY_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.TURN_ON_NOTIFICATION_PATH;
import static com.group4.khoatritoan.k_it.repository.MyKey.CURRENT_MINUTE;
import static com.group4.khoatritoan.k_it.repository.MyKey.CURRENT_SECOND;
import static com.group4.khoatritoan.k_it.repository.MyKey.END_TIME;
import static com.group4.khoatritoan.k_it.repository.MyKey.IS_AUTO_MODE_ENABLED;
import static com.group4.khoatritoan.k_it.repository.MyKey.IS_TURN_ON_MODE;
import static com.group4.khoatritoan.k_it.repository.MyKey.MAX_TIME;
import static com.group4.khoatritoan.k_it.repository.MyKey.MIN_TIME;
import static com.group4.khoatritoan.k_it.repository.MyKey.START_TIME;
import static com.group4.khoatritoan.k_it.repository.MyKey.TURN_ON_NOTIFICATION;
import static com.group4.khoatritoan.k_it.repository.MyKey.VISIBILITY_1;
import static com.group4.khoatritoan.k_it.repository.MyKey.VISIBILITY_2;


public class HomeModel {

	//#region property - constructor

	private final static FirebaseDatabase database = FirebaseDatabase.getInstance();

	private final SavedStateHandle stateHandle;
	private final LocalRepository repository;

	public HomeModel(Context context) {

		Log.e("Home>HomeModel", "init");

		stateHandle = new SavedStateHandle();
		repository = new LocalRepository(context);
	}

	//#endregion

	//#region TurnOnNotificationGroup

	private void syncTurnOnNotificationWithLocal(boolean value) {
		repository.setTurnOnNotification(value);
	}

	public void setTurnOnNotification(boolean value, OnCompleteListener<Void> callback) {
		DatabaseReference ref = database.getReference(TURN_ON_NOTIFICATION_PATH);
		ref.setValue(value)
			.addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					syncTurnOnNotificationWithLocal(value);
				}
			})
			.addOnCompleteListener(callback);
	}

	public MutableLiveData<Boolean> getTurnOnNotification() {

		Log.e("Home>HomeModel", "getTurnOnNotification");

		MutableLiveData<Boolean> turnOnNotification = stateHandle.getLiveData(TURN_ON_NOTIFICATION);

//		boolean initialValue = repository.getTurnOnNotification();
//		turnOnNotification.setValue(initialValue);

		DatabaseReference ref = database.getReference(TURN_ON_NOTIFICATION_PATH);
		ref.keepSynced(true);
		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
				Boolean value = snapshot.getValue(Boolean.class);
				Log.e( "Home>HomeModel", "addValueEventListener success: " + value);
				turnOnNotification.setValue(value);
			}

			@Override
			public void onCancelled(@NonNull @NotNull DatabaseError error) {
				Log.e( "Home>HomeModel", "addValueEventListener error: " + error);
			}
		});

		turnOnNotification.observeForever(this::syncTurnOnNotificationWithLocal);

		return turnOnNotification;
	}

	//#endregion
	//#region DelayNotificationSecondsGroup

	public MutableLiveData<Pair<Integer, Integer>> getMaxTime() {

		Log.e("Home>HomeModel", "getMaxTime");

		MutableLiveData<Pair<Integer, Integer>> maxTime =
				stateHandle.getLiveData(MAX_TIME, new Pair<>(0, 0));

		DatabaseReference ref = database.getReference(MAX_DELAY_SECONDS_PATH);
		ref.get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				DataSnapshot snapshot = task.getResult();
				int rawSeconds = snapshot.getValue(Integer.class);
				maxTime.setValue(new Pair<>(
						rawSeconds / 60,
						rawSeconds % 60
				));
				Log.e("Home>HomeModel", "maxTime success: " + maxTime);
			}
			else {
				Log.e("Home>HomeModel","maxTime error: " + task.getException());
			}
		});

		return maxTime;
	}
	public MutableLiveData<Pair<Integer, Integer>> getMinTime() {

		Log.e("Home>HomeModel", "getMinTime");

		MutableLiveData<Pair<Integer, Integer>> minTime =
				stateHandle.getLiveData(MIN_TIME, new Pair<>(0, 0));

		DatabaseReference ref = database.getReference(MIN_DELAY_SECONDS_PATH);
		ref.get().addOnCompleteListener(task -> {
			if (!task.isSuccessful()) {
				Log.e("Home>HomeModel", "minTime error: " + task.getException());
				return;
			}

			DataSnapshot snapshot = task.getResult();
			Integer rawSeconds = snapshot.getValue(Integer.class);
			minTime.setValue(new Pair<> (
					rawSeconds / 60,
					rawSeconds % 60
			));
			Log.e("Home>HomeModel", "minTime success: " + minTime);
		});

		return minTime;
	}
	public Map<String, MutableLiveData<Integer>> getCurrentTime() {

		Log.e("Home>HomeModel", "getCurrentTime");

		Map<String, MutableLiveData<Integer>> currentTime = new HashMap<>(2);
		currentTime.put(CURRENT_SECOND, stateHandle.getLiveData(CURRENT_SECOND));
		currentTime.put(CURRENT_MINUTE, stateHandle.getLiveData(CURRENT_MINUTE));

		DatabaseReference ref = database.getReference(DELAY_NOTIFICATION_SECONDS_PATH);
		ref.get().addOnCompleteListener(task -> {
			if (!task.isSuccessful()) {
				Log.e("Home>HomeModel", "curTime error: " + task.getException());

				return;
			}

			DataSnapshot snapshot = task.getResult();
			Integer rawSeconds = snapshot.getValue(Integer.class);
			// Không được thay đổi vị trí của hai dòng bên dưới này
			// vì minute phụ thuộc vào second
			currentTime.get(CURRENT_SECOND).setValue(rawSeconds % 60);
			currentTime.get(CURRENT_MINUTE).setValue(rawSeconds / 60);
			Log.e("Home>HomeModel", "curTime success: " + rawSeconds);
		});

		return currentTime;
	}

	public MutableLiveData<Integer> getVisibility1() {
		Log.e("Home>HomeModel", "getVisibility1");
		return stateHandle.getLiveData(VISIBILITY_1, View.GONE);
	}

	public void setDelayNotificationSeconds(int value, OnCompleteListener<Void> callback) {
		Log.e("Home>HomeModel", "setDelayNotificationSeconds: " + value);
		DatabaseReference ref = database.getReference(DELAY_NOTIFICATION_SECONDS_PATH);
		ref.setValue(value).addOnCompleteListener(callback);
	}

	//#endregion
	//#region AutoModeGroup

	public boolean getHasReceiver() {
		return repository.getIsAutoModeEnabled();
	}
	public MutableLiveData<Boolean> getIsAutoModeEnabled() {

		MutableLiveData<Boolean> isAutoModeEnabled = stateHandle.getLiveData(IS_AUTO_MODE_ENABLED);
		boolean initialValue = repository.getIsAutoModeEnabled();
		isAutoModeEnabled.setValue(initialValue);

		return isAutoModeEnabled;
	}
	public void setIsAutoModeEnabled(boolean value) {
		repository.setIsAutoModeEnabled(value);
	}


	public MutableLiveData<Boolean> getIsTurnOnMode() {
		MutableLiveData<Boolean> isTurnOnMode = stateHandle.getLiveData(IS_TURN_ON_MODE);
		boolean initialValue = repository.getIsTurnOnMode();
		isTurnOnMode.setValue(initialValue);

		return isTurnOnMode;
	}
	public void setIsTurnOnMode(boolean value) {
		repository.setIsTurnOnMode(value);
	}

	public MutableLiveData<Long> getStartTime() {

		MutableLiveData<Long> startTime = stateHandle.getLiveData(START_TIME);
		long initialValue = repository.getStartTime();
		startTime.setValue(initialValue);

		return startTime;

	}
	public void setStartTime(long value) {
		repository.setStartTime(value);
	}

	public MutableLiveData<Long> getEndTime() {

		MutableLiveData<Long> endTime = stateHandle.getLiveData(END_TIME);
		long initialValue = repository.getEndTime();
		endTime.setValue(initialValue);

		return endTime;
	}
	public void setEndTime(long value) {
		repository.setEndTime(value);
	}

	public MutableLiveData<Integer> getVisibility2() {
		return stateHandle.getLiveData(VISIBILITY_2, View.GONE);
	}

	//#endregion
}