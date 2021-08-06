package com.group4.khoatritoan.k_it.service;

import android.app.AlarmManager;
import android.util.Log;;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.AlarmManagerHelper;


public class EndTimeReceiver extends TimeReceiver {

	@Override
	protected void repeating() {

		long triggerMilliseconds = model.getEndTime().getValue();
		triggerMilliseconds += AlarmManager.INTERVAL_DAY;
		model.setEndTime(triggerMilliseconds);

		Log.e("AlarmManager", "next end at: " + triggerMilliseconds);

		AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper(context);
		alarmManagerHelper.enforce(EndTimeReceiver.class, triggerMilliseconds);
	}

	@Override
	protected void updateTurnOnNotification() {
		model.setTurnOnNotification(!isTurnOnMode, task -> {
			if (!task.isSuccessful()) {
				Log.e("AlarmManager", "end - set turnOnNotification error: " + task.getException());
				return;
			}

			Log.e("AlarmManager", "end - set turnOnNotification success: " + !isTurnOnMode);

			String contentTitle = context.getString(R.string.title_end_change_turn_on_notification);
			String contentText = isTurnOnMode ?
					context.getString(R.string.label_turn_off):
					context.getString(R.string.label_turn_on);
			notifyToClient(contentTitle, contentText);
		});
	}
}