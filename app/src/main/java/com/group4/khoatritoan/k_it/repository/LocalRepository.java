package com.group4.khoatritoan.k_it.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import static android.content.Context.MODE_PRIVATE;
import static com.group4.khoatritoan.k_it.repository.MyKey.END_TIME;
import static com.group4.khoatritoan.k_it.repository.MyKey.HAS_RECEIVER;
import static com.group4.khoatritoan.k_it.repository.MyKey.IS_AUTO_MODE_ENABLED;
import static com.group4.khoatritoan.k_it.repository.MyKey.IS_FIRST_TIME_LAUNCH_APPLICATION;
import static com.group4.khoatritoan.k_it.repository.MyKey.IS_TURN_ON_MODE;
import static com.group4.khoatritoan.k_it.repository.MyKey.START_TIME;
import static com.group4.khoatritoan.k_it.repository.MyKey.TURN_ON_NOTIFICATION;
import static com.group4.khoatritoan.k_it.repository.MyKey.VISIBILITY_2;
import static com.group4.khoatritoan.k_it.custom.Utility.getStartCurrentDateMilliseconds;

public class LocalRepository {

	private final static String NAME = "K-IT";
	private final SharedPreferences sp;

	public LocalRepository(Context context) {
		sp = context.getSharedPreferences(NAME, MODE_PRIVATE);
	}


	//#region public getter-setter

	//#region For LoginModel

	public boolean getIsFirstTimeLaunchApplication() {
		return getBoolean(IS_FIRST_TIME_LAUNCH_APPLICATION, true);
	}
	public void setIsFirstTimeLaunchApplication(boolean value) {
		putBoolean(IS_FIRST_TIME_LAUNCH_APPLICATION, value);
	}

	//#endregion

	//#region For HomeModel

	public boolean getTurnOnNotification() {
		return getBoolean(TURN_ON_NOTIFICATION, false);
	}
	public void setTurnOnNotification(boolean value) {
		putBoolean(TURN_ON_NOTIFICATION, value);
	}

	public boolean getIsAutoModeEnabled() {
		return getBoolean(IS_AUTO_MODE_ENABLED, false);
	}
	public void setIsAutoModeEnabled(boolean value) {
		putBoolean(IS_AUTO_MODE_ENABLED, value);
	}

	public boolean getIsTurnOnMode() {
		return getBoolean(IS_TURN_ON_MODE, true);
	}
	public void setIsTurnOnMode(boolean value) {
		putBoolean(IS_TURN_ON_MODE, value);
	}

	public long getStartTime() {
		return getLong(START_TIME, getStartCurrentDateMilliseconds());
	}
	public void setStartTime(long value) {
		putLong(START_TIME, value);
	}

	public long getEndTime() {
		return getLong(END_TIME, getStartCurrentDateMilliseconds());
	}
	public void setEndTime(long value) {
		putLong(END_TIME, value);
	}

	public int getVisibility2() {
		return getInt(VISIBILITY_2, View.GONE);
	}

	public boolean getHasReceiver() {
		return getBoolean(HAS_RECEIVER, false);
	}
	public void setHasReceiver(boolean value) {
		putBoolean(HAS_RECEIVER, value);
	}

	public void resetHomeConfig() {
		setTurnOnNotification(false);
		setIsAutoModeEnabled(false);
		setHasReceiver(false);

		long time = getStartCurrentDateMilliseconds();
		setStartTime(time);
		setEndTime(time);
	}

	//#endregion

	//#endregion
	//#region private getter-setter

//	private String getString(String key, String defaultValue) {
//		return sp.getString(key, defaultValue);
//	}
//	private void putString(String key, String value) {
//		sp.edit().putString(key, value).apply();
//	}

	private int getInt(String key, int defaultValue) {
		return sp.getInt(key, defaultValue);
	}
	private void putInt(String key, int value) {
		sp.edit().putInt(key, value).apply();
	}

	private long getLong(String key, long defaultValue) {
		return sp.getLong(key, defaultValue);
	}
	private void putLong(String key, long value) {
		sp.edit().putLong(key, value).apply();
	}

	private boolean getBoolean(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}
	private void putBoolean(String key, boolean value) {
		sp.edit().putBoolean(key, value).apply();
	}

	//#endregion
}