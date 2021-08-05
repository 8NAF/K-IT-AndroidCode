package com.group4.khoatritoan.k_it.model;

import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group4.khoatritoan.k_it.custom.Utility;
import com.group4.khoatritoan.k_it.entity.Log;
import com.group4.khoatritoan.k_it.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.group4.khoatritoan.k_it.repository.DatabasePath.LOGS_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.START;
import static com.group4.khoatritoan.k_it.repository.MyKey.IS_NEWEST;
import static com.group4.khoatritoan.k_it.repository.MyKey.LOGS;
import static com.group4.khoatritoan.k_it.repository.MyKey.MAP_BUTTONS;
import static com.group4.khoatritoan.k_it.repository.MyKey.START_END_MILLISECONDS;

public class LogModel {

	private final SavedStateHandle stateHandle = new SavedStateHandle();

	//#region Singleton
	private LogModel() { }
	private static LogModel instance;
	public static LogModel getInstance() {
		if (instance == null) {
			instance = new LogModel();
		}
		return instance;
	}
	//#endregion

	//#region getter
	public MutableLiveData<Pair<Long, Long>> getStartEndMilliseconds() {
		return stateHandle.getLiveData(START_END_MILLISECONDS, new Pair<>(
				Utility.getStartCurrentDateMilliseconds(),
				Utility.getStartCurrentDateMilliseconds() + 86399999
		));
	}

	public MutableLiveData<List<Log>> getLogs() {
		return stateHandle.getLiveData(LOGS, new ArrayList<>());
	}

	public MutableLiveData<Boolean> getIsNewest() {
		return stateHandle.getLiveData(IS_NEWEST, true);
	}

	public MutableLiveData<Map<Integer, Boolean>> getMapButtons() {

		Map<Integer, Boolean> mapButtons = new HashMap<>();
		mapButtons.put(R.id.btnTrue, true);
		mapButtons.put(R.id.btnFalse, true);

		return stateHandle.getLiveData(MAP_BUTTONS, mapButtons);
	}
	//#endregion


	public void queryLogs(Pair<Long, Long> limit, ValueEventListener listener) {

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(LOGS_PATH);
		Query query = ref
				.orderByChild(START)
				.startAt(limit.first)
				.endAt(limit.second);
//				.limitToFirst(10);

		query.addListenerForSingleValueEvent(listener);
	}
}