package com.group4.khoatritoan.k_it.ui.main.tab.log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.core.util.Predicate;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.Utility;
import com.group4.khoatritoan.k_it.entity.Log;
import com.group4.khoatritoan.k_it.model.LogModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LogViewModel extends ViewModel {

	//#region property - constructor - getter - setter

	private final LogModel model;

	private final MutableLiveData<Pair<Long, Long>> startEndMillisecond;
	private final MutableLiveData<List<Log>> logs;
	private final MutableLiveData<Boolean> isNewest;
	private final MutableLiveData<Map<Integer, Boolean>> mapButtons;

	public LogViewModel() {

		model = LogModel.getInstance();

		startEndMillisecond = model.getStartEndMilliseconds();
		isNewest = model.getIsNewest();
		mapButtons = model.getMapButtons();
		logs = model.getLogs();
	}


	public MutableLiveData<Pair<Long, Long>> getStartEndMilliseconds() {
		return startEndMillisecond;
	}
	public MutableLiveData<List<Log>> getLogs() {
		return logs;
	}
	public MutableLiveData<Boolean> getIsNewest() {
		return isNewest;
	}
	public MutableLiveData<Map<Integer, Boolean>> getMapButtons() {
		return mapButtons;
	}

	//#endregion
	//#region on-methods

	public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

		Map<Integer, Boolean> map = new HashMap<>(mapButtons.getValue());
		map.put(checkedId, isChecked);
		mapButtons.setValue(map);
	}
	public void onPositiveButtonClick(Pair<Long, Long> selection) {
		// Selection tr??? v??? millisecond theo UTC+00:00
		// trong khi ta c???n l?? ng??y theo timezone c???a ng?????i d??ng
		// n??n ph???i tr??? ??i offset c???a timezone hi???n t???i th?? m???i ra k???t qu??? m?? ta c???n.
		// C??n ?????i v???i end th?? ph???i i??nh ?????n h???t ng??y n??n ta s??? c???ng th??m 23:59:59.999 (86399999ms)
		Pair<Long, Long> timezoneSelection = new Pair<>(
				selection.first - Utility.getCurrentOffsetMilliseconds(),
				selection.second - Utility.getCurrentOffsetMilliseconds() + 86399999
		);

		android.util.Log.e("start", timezoneSelection.first + "");
		android.util.Log.e("start", timezoneSelection.second + "");

		// N???u m?? gi?? tr??? thay ?????i th?? m???i g??n
		if (!timezoneSelection.equals(startEndMillisecond.getValue())) {
			startEndMillisecond.setValue(timezoneSelection);
		}
	}
	public void onStartEndMillisecondsChange(Pair<Long, Long> value) {

		model.queryLogs(value, new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

				// Danh s??ch l???y v??? t??? database lu??n ???????c s???p x???p gi???m d???n (t???c m???i nh???t ??? ?????u)
				LinkedList<Log> list = new LinkedList<>();
				for (DataSnapshot child: snapshot.getChildren()) {
					Log log = child.getValue(Log.class);
					list.addFirst(log);
				}

				if (!Objects.equals(logs.getValue(), list)) {
					logs.setValue(list);
				}
			}

			@Override
			public void onCancelled(@NonNull @NotNull DatabaseError error) { }
		});

	}

	//#endregion
	//#region filter methods

	public List<Log> filterLogs() {

		List<Log> newList;
		newList = receivedFilter(logs.getValue());
		newList = newestFilter(newList);
		return newList;
	}

	public List<Log> receivedFilter(List<Log> originalList) {

		if (originalList == null) {
			return new ArrayList<>();
		}

		Map<Integer, Boolean> map = mapButtons.getValue();
		if (map.get(R.id.btnTrue) && map.get(R.id.btnFalse)) {
			return new ArrayList<>(originalList);
		}

		List<Log> newList = new ArrayList<>();
		Predicate<Boolean> checkFunction =
				map.get(R.id.btnTrue)    ?
				isReceived -> isReceived :
				isReceived -> !isReceived;

		for (Log log : originalList) {
			if (checkFunction.test(log.isReceived)) {
				newList.add(log);
			}
		}

		return newList;
	}

	public List<Log> newestFilter(List<Log> originalList) {

		List<Log> newList = new ArrayList<>(originalList);

		Boolean isNewest = getIsNewest().getValue();
		if (!isNewest) {
			Collections.reverse(newList);
		}

		return newList;
	}

	//#endregion
}