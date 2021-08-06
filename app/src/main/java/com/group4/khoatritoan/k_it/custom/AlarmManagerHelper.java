package com.group4.khoatritoan.k_it.custom;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.group4.khoatritoan.k_it.service.EndTimeReceiver;
import com.group4.khoatritoan.k_it.service.StartTimeReceiver;

public class AlarmManagerHelper {

	Context context;
	public AlarmManagerHelper(Context context) {
		this.context = context;
	}

	//#region enforce

	public void enforce(long startTime, long endTime) {

		if (startTime >= endTime) {
			Log.e("AlarmManager", "cancel because of start >= end");
			return;
		}

		enforce(StartTimeReceiver.class, startTime);
		enforce(EndTimeReceiver.class, endTime);
	}

	public void enforce(Class<? extends BroadcastReceiver> className, long triggerMilliseconds) {

		Intent intent = new Intent(context, className);
		PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMilliseconds, pi);
	}

	//#endregion
	//#region cancel

	public void cancel() {

		cancel(StartTimeReceiver.class);
		cancel(EndTimeReceiver.class);
	}

	private void cancel(Class<? extends BroadcastReceiver> className) {

		Log.e("AlarmManager", "cancel: " + className.getName());

		Intent intent = new Intent(context, className);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	//#endregion
}