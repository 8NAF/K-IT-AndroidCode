package com.group4.khoatritoan.k_it.service;

import android.app.AlarmManager;
import android.util.Log;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.AlarmManagerHelper;


public class StartTimeReceiver extends TimeReceiver {

	@Override
	protected void updateTurnOnNotification() {
		model.setTurnOnNotification(isTurnOnMode, task -> {

			if (!task.isSuccessful()) {
				Log.e("AlarmManager", "start - set turnOnNotification error: " + task.getException());
				return;
			}

			Log.e("AlarmManager", "start - set turnOnNotification success: " + isTurnOnMode);

			String contentTitle = context
					.getString(R.string.title_start_change_turn_on_notification);
			String contentText = isTurnOnMode ?
					context.getString(R.string.label_turn_on):
					context.getString(R.string.label_turn_off);
			notifyToClient(contentTitle, contentText);
		});
	}

	@Override
	protected void repeating() {

		long triggerMilliseconds = model.getStartTime().getValue();
		triggerMilliseconds += AlarmManager.INTERVAL_DAY;
		model.setStartTime(triggerMilliseconds);

		Log.e("AlarmManager", "next start at: " + triggerMilliseconds);

		AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper(context);
		alarmManagerHelper.enforce(StartTimeReceiver.class, triggerMilliseconds);
	}
}