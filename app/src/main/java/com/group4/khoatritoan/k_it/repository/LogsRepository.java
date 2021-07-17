package com.group4.khoatritoan.k_it.repository;

import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.group4.khoatritoan.k_it.custom.Utility;
import com.group4.khoatritoan.k_it.model.NotificationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LogsRepository {

	private SavedStateHandle stateHandle = new SavedStateHandle();
	private LogsRepository() {}

	private static LogsRepository instance;
	public static LogsRepository getInstance() {

		if (instance == null) {
			instance = new LogsRepository();
		}

		return instance;
	}

	private final static String LIST_NOTIFICATIONS = "LIST_NOTIFICATIONS";
	private final static String START_END_MILLISECONDS = "START_END_MILLISECONDS";
	private final static String IS_NEWEST = "IS_NEWEST";
	private final static String FILTER_IS_RECEIVED = "FILTER_IS_RECEIVED";

	public MutableLiveData<Pair<Long, Long>> getStartEndMilliseconds() {

		return stateHandle.getLiveData(START_END_MILLISECONDS, new Pair<>(
				Utility.getStartCurrentDateMilliseconds(),
				Utility.getStartCurrentDateMilliseconds() + 86399999
		));
	}

	public MutableLiveData<List<NotificationModel>> getListNotifications() {
		return stateHandle.getLiveData(LIST_NOTIFICATIONS, new ArrayList<>());
	}

	public MutableLiveData<Boolean> getIsNewest() {
		return stateHandle.getLiveData(IS_NEWEST, true);
	}

	public MutableLiveData<Boolean> getFilterIsReceived() {
		return stateHandle.getLiveData(FILTER_IS_RECEIVED, null);
	}
}
