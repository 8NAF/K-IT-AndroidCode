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
import com.group4.khoatritoan.k_it.custom.LocalRepository;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.group4.khoatritoan.k_it.custom.DatabasePath.DELAY_NOTIFICATION_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.custom.DatabasePath.MAX_DELAY_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.custom.DatabasePath.MIN_DELAY_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.custom.DatabasePath.TURN_ON_NOTIFICATION_PATH;
import static com.group4.khoatritoan.k_it.custom.MyKey.CURRENT_MINUTE;
import static com.group4.khoatritoan.k_it.custom.MyKey.CURRENT_SECOND;
import static com.group4.khoatritoan.k_it.custom.MyKey.END_TIME;
import static com.group4.khoatritoan.k_it.custom.MyKey.IS_AUTO_MODE_ENABLED;
import static com.group4.khoatritoan.k_it.custom.MyKey.IS_TURN_ON_MODE;
import static com.group4.khoatritoan.k_it.custom.MyKey.MAX_TIME;
import static com.group4.khoatritoan.k_it.custom.MyKey.MIN_TIME;
import static com.group4.khoatritoan.k_it.custom.MyKey.START_TIME;
import static com.group4.khoatritoan.k_it.custom.MyKey.TURN_ON_NOTIFICATION;
import static com.group4.khoatritoan.k_it.custom.MyKey.VISIBILITY_1;
import static com.group4.khoatritoan.k_it.custom.MyKey.VISIBILITY_2;


public class HomeModel {

	//#region property - constructor

	private final static FirebaseDatabase database = FirebaseDatabase.getInstance();

	private final SavedStateHandle stateHandle;
	private final LocalRepository repository;

	public HomeModel(Context context) {
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

		MutableLiveData<Boolean> turnOnNotification = stateHandle.getLiveData(TURN_ON_NOTIFICATION);

//		boolean initialValue = repository.getTurnOnNotification();
//		turnOnNotification.setValue(initialValue);

		DatabaseReference ref = database.getReference(TURN_ON_NOTIFICATION_PATH);
		ref.keepSynced(true);
		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
				Boolean value = snapshot.getValue(Boolean.class);
				Log.e("From", "addValueEventListener: " + value);
				turnOnNotification.setValue(value);
			}

			@Override
			public void onCancelled(@NonNull @NotNull DatabaseError error) { }
		});

		turnOnNotification.observeForever(this::syncTurnOnNotificationWithLocal);

		return turnOnNotification;
	}

	//#endregion
	//#region DelayNotificationSecondsGroup

	public MutableLiveData<Pair<Integer, Integer>> getMaxTime() {

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
				Log.e("maxTime success", maxTime.getValue() + "");
			}
			else {
				Log.e("maxTime error", task.getException().getMessage());
			}
		});

		return maxTime;
	}
	public MutableLiveData<Pair<Integer, Integer>> getMinTime() {

		MutableLiveData<Pair<Integer, Integer>> minTime =
				stateHandle.getLiveData(MIN_TIME, new Pair<>(0, 0));

		DatabaseReference ref = database.getReference(MIN_DELAY_SECONDS_PATH);
		ref.get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				DataSnapshot snapshot = task.getResult();
				Integer rawSeconds = snapshot.getValue(Integer.class);
				minTime.setValue(new Pair<> (
						rawSeconds / 60,
						rawSeconds % 60
				));
				Log.e("minTime success", minTime + "");
			}
			else {
				Log.e("minTime error", task.getException().getMessage());
			}
		});

		return minTime;
	}
	public Map<String, MutableLiveData<Integer>> getCurrentTime() {

		Map<String, MutableLiveData<Integer>> currentTime = new HashMap<>(2);
		currentTime.put(CURRENT_SECOND, stateHandle.getLiveData(CURRENT_SECOND));
		currentTime.put(CURRENT_MINUTE, stateHandle.getLiveData(CURRENT_MINUTE));

		DatabaseReference ref = database.getReference(DELAY_NOTIFICATION_SECONDS_PATH);
		ref.get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				DataSnapshot snapshot = task.getResult();
				Integer rawSeconds = snapshot.getValue(Integer.class);
				// Không được thay đổi vị trí của hai dòng bên dưới này
				// vì minute phụ thuộc vào second
				currentTime.get(CURRENT_SECOND).setValue(rawSeconds % 60);
				currentTime.get(CURRENT_MINUTE).setValue(rawSeconds / 60);
				Log.e("curTime success", rawSeconds + "");
			}
			else {
				Log.e("curTime error", task.getException().getMessage());
			}
		});

		return currentTime;
	}

	public MutableLiveData<Integer> getVisibility1() {
		return stateHandle.getLiveData(VISIBILITY_1, View.GONE);
	}

	public void setDelayNotificationSeconds(int value, OnCompleteListener<Void> callback) {
		DatabaseReference ref = database.getReference(DELAY_NOTIFICATION_SECONDS_PATH);
		ref.setValue(value).addOnCompleteListener(callback);
	}

	//#endregion
	//#region AutoModeGroup

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

	public MutableLiveData<String> getStartTime() {

		MutableLiveData<String> startTime = stateHandle.getLiveData(START_TIME);
		String initialValue = repository.getStartTime();
		startTime.setValue(initialValue);

		return startTime;

	}
	public void setStartTime(String value) {
		repository.setStartTime(value);
	}

	public MutableLiveData<String> getEndTime() {

		MutableLiveData<String> endTime = stateHandle.getLiveData(END_TIME);
		String initialValue = repository.getEndTime();
		endTime.setValue(initialValue);

		return endTime;
	}
	public void setEndTime(String value) {
		repository.setEndTime(value);
	}

	public MutableLiveData<Integer> getVisibility2() {
		MutableLiveData<Integer> visibility2 = stateHandle.getLiveData(VISIBILITY_2);
		int initialValue = repository.getVisibility2();
		visibility2.setValue(initialValue);

		return visibility2;
	}

	public boolean getHasReceiver() {
		return repository.getHasReceiver();
	}
	public void setHasReceiver(boolean value) {
		repository.setHasReceiver(value);
	}

//#endregion
}