package com.group4.khoatritoan.k_it.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.DELAY_NOTIFICATION_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.MAX_DELAY_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.MIN_DELAY_SECONDS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.TURN_ON_NOTIFICATION_PATH;

public class HomeRepository {


	private SavedStateHandle stateHandle = new SavedStateHandle();
	private HomeRepository() {}



	private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
	private static HomeRepository instance;
	public static HomeRepository getInstance() {
		if (instance == null) {
			instance = new HomeRepository();
		}
		return instance;
	}



	public final static String TURN_ON_NOTIFICATION = "TURN_ON_NOTIFICATION";

	public void setTurnOnNotification(Context context, Boolean value) {
		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		sp.edit().putBoolean(TURN_ON_NOTIFICATION, value).apply();
	}


	public MutableLiveData<Boolean> getIsTurnOnNotification(Context context) {

		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		boolean initialValue = sp.getBoolean(TURN_ON_NOTIFICATION, false);

		MutableLiveData<Boolean> isTurnOnNotification = stateHandle.getLiveData(TURN_ON_NOTIFICATION);
		isTurnOnNotification.setValue(initialValue);

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(TURN_ON_NOTIFICATION_PATH);
		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
				Boolean value = snapshot.getValue(Boolean.class);
				Log.e("From", "addValueEventListener: " + value);
				isTurnOnNotification.setValue(value);
			}

			@Override
			public void onCancelled(@NonNull @NotNull DatabaseError error) { }
		});

		return isTurnOnNotification;

//		DatabaseReference ref = database.getReference(TURN_ON_NOTIFICATION_PATH);
//		ref.get().addOnCompleteListener(task -> {
//			if (task.isSuccessful()) {
//				DataSnapshot snapshot = task.getResult();
//				Boolean value = snapshot.getValue(Boolean.class);
//				isTurnOnNotification.setValue(value);
//				Log.e("TON Success", value  + "");
//			}
//			else {
//				Log.e("TON Error", task.getException() + "");
//			}
//		});

//		ref.addValueEventListener(new ValueEventListener() {
//			@Override
//			public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//				Boolean value = snapshot.getValue(Boolean.class);
//				isTurnOnNotification.setValue(value);
//				Log.e("TON Success", value  + "");
//			}
//
//			@Override
//			public void onCancelled(@NonNull @NotNull DatabaseError error) {
//				Log.e("TON Error", error.getMessage());
//			}
//		});

	}




	public final static String MAX_TIME = "MAX_TIME";
	public final static String MIN_TIME = "MIN_TIME";
	public final static String CURRENT_MINUTE = "CURRENT_MINUTE";
	public final static String CURRENT_SECOND = "CURRENT_SECOND";

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
				int rawSeconds = snapshot.getValue(Integer.class);
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
		currentTime.put(CURRENT_SECOND, stateHandle.getLiveData(CURRENT_SECOND, 0));
		currentTime.put(CURRENT_MINUTE, stateHandle.getLiveData(CURRENT_MINUTE, 0));

		DatabaseReference ref = database.getReference(DELAY_NOTIFICATION_SECONDS_PATH);
		ref.get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				DataSnapshot snapshot = task.getResult();
				int rawSeconds = snapshot.getValue(Integer.class);
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


	public final static String AUTO_MODE_GROUP = "AUTO_MODE_GROUP";
	public final static String AUTO_MODE_IS_ENABLED = "AUTO_MODE_IS_ENABLED";
	public final static String IS_TURN_ON_MODE = "IS_TURN_ON_MODE";
	public final static String START_TIME = "START_TIME";
	public final static String END_TIME = "END_TIME";
	public final static String HAS_RECEIVER = "HAS_RECEIVER";

	public MutableLiveData<Boolean> getAutoModeIsEnabled(Context context) {

		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		boolean initialValue = sp.getBoolean(AUTO_MODE_IS_ENABLED, false);

		MutableLiveData<Boolean> autoModeIsEnabled = stateHandle.getLiveData(AUTO_MODE_IS_ENABLED);
		autoModeIsEnabled.setValue(initialValue);

		return autoModeIsEnabled;
	}
	public MutableLiveData<Boolean> getIsTurnOnMode(Context context) {

		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		boolean initialValue = sp.getBoolean(IS_TURN_ON_MODE, true);

		MutableLiveData<Boolean> isTurnMode = stateHandle.getLiveData(IS_TURN_ON_MODE);
		isTurnMode.setValue(initialValue);

		return isTurnMode;
	}
	public MutableLiveData<String> getStartTime(Context context) {

		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		String initialValue = sp.getString(START_TIME, "00:00");

		MutableLiveData<String> startTime = stateHandle.getLiveData(START_TIME);
		startTime.setValue(initialValue);

		return startTime;
	}
	public MutableLiveData<String> getEndTime(Context context) {

		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		String initialValue = sp.getString(END_TIME, "00:00");

		MutableLiveData<String> endTime = stateHandle.getLiveData(END_TIME);
		endTime.setValue(initialValue);

		return endTime;
	}

	public boolean hasReceiver(Context context) {

		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		return sp.getBoolean(HAS_RECEIVER, false);
	}

	public void setHasReceiver(Context context, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
		sp.edit().putBoolean(HAS_RECEIVER, value).apply();
	}
}