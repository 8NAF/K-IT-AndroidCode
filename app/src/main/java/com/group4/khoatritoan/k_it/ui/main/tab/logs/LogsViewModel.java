package com.group4.khoatritoan.k_it.ui.main.tab.logs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.Utility;
import com.group4.khoatritoan.k_it.model.NotificationModel;
import com.group4.khoatritoan.k_it.repository.LogsRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.LOGS_PATH;

public class LogsViewModel extends ViewModel {

	private static final LogsRepository repository = LogsRepository.getInstance();

	private MutableLiveData<Pair<Long, Long>> startEndMillisecond;
	private MutableLiveData<List<NotificationModel>> listNotifications;
	private MutableLiveData<Boolean> isNewest;
	private MutableLiveData<Boolean> filterIsReceived;

	public LogsViewModel() {
		startEndMillisecond = repository.getStartEndMilliseconds();
		listNotifications = repository.getListNotifications();
		isNewest = repository.getIsNewest();
		filterIsReceived = repository.getFilterIsReceived();
	}

	public MutableLiveData<Pair<Long, Long>> getStartEndMilliseconds() {
		return startEndMillisecond;
	}
	public MutableLiveData<List<NotificationModel>> getListNotifications() {
		return listNotifications;
	}
	public MutableLiveData<Boolean> getIsNewest() {
		return isNewest;
	}
	public MutableLiveData<Boolean> getFilterIsReceived() {
		return filterIsReceived;
	}


	public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

		List<Integer> checkedIds = group.getCheckedButtonIds();

		if (checkedIds.size() == 2) {
			filterIsReceived.setValue(null);
		}
		else if (isChecked) {
			filterIsReceived.setValue(checkedId == R.id.btnTrue);
		}
		else {
			filterIsReceived.setValue(checkedId != R.id.btnTrue);
		}


		Log.e("onButtonChecked", filterIsReceived.getValue() + "");
	}

	public void onPositiveButtonClick(Pair<Long, Long> selection) {
		// Selection trả về millisecond theo UTC+00:00
		// trong khi ta cần là ngày theo timezone của người dùng
		// nên phải trừ đi offset của timezone hiện tại thì mới ra kết quả mà ta cần.
		// Còn đối với end thì phải iính đến hết ngày nên ta sẽ cộng thêm 23:59:59.999 (86399999ms)
		Pair<Long, Long> timezoneSelection = new Pair<>(
				selection.first - Utility.getCurrentOffsetMilliseconds(),
				selection.second - Utility.getCurrentOffsetMilliseconds() + 86399999
		);

		Log.e("start", timezoneSelection.first + "");
		Log.e("end", timezoneSelection.second + "");

		// Nếu mà giá trị thay đổi thì mới gán
		if (!timezoneSelection.equals(startEndMillisecond.getValue())) {
			startEndMillisecond.setValue(timezoneSelection);
		}
	}

	public void onStartEndMillisecondsChange(@NonNull @NotNull Pair<Long, Long> value) {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(LOGS_PATH);
		Query query = ref
				.orderByChild("start")
				.startAt(value.first)
				.endAt(value.second);
//					.limitToFirst(10);

		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

				// Danh sách lấy về từ database luôn được sắp xếp giảm dần (tức mới nhất ở đầu)
				LinkedList<NotificationModel> newList = new LinkedList<>();
				for (DataSnapshot child: snapshot.getChildren()) {
					NotificationModel notification = child.getValue(NotificationModel.class);
					newList.addFirst(notification);
				}

				listNotifications.setValue(newList);
			}

			@Override
			public void onCancelled(@NonNull @NotNull DatabaseError error) { }
		});
	}

	public List<NotificationModel> transformListNotifications() {

		List<NotificationModel> originalList = listNotifications.getValue();
		if (originalList == null) {
			return new ArrayList<>();
		}

		List<NotificationModel> newList = null;

		Boolean filterIsReceived = getFilterIsReceived().getValue();
		if (filterIsReceived != null) {
			newList = new ArrayList<>();
			for (NotificationModel notification : originalList) {
				if (filterIsReceived.equals(notification.isReceived)) {
					newList.add(notification);
				}
			}
		}
		else {
			newList = new ArrayList<>(originalList);
		}

		Boolean isNewest = getIsNewest().getValue();
		if (!isNewest) {
			Collections.reverse(newList);
		}
		return newList;
	}
}