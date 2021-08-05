package com.group4.khoatritoan.k_it.custom;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.group4.khoatritoan.k_it.service.EndTimeReceiver;
import com.group4.khoatritoan.k_it.service.StartTimeReceiver;

import java.util.Calendar;

public class AlarmManagerHelper {

	Context context;
	public AlarmManagerHelper(Context context) {
		this.context = context;
	}

	//#region enforce

	public void enforce(long startTime, long endTime) {

		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.set(Calendar.SECOND, 0);
		currentCalendar.set(Calendar.MILLISECOND, 0);
		long currentTime = currentCalendar.getTimeInMillis();

		Log.e("AlarmManager", "enforce, before start: " + startTime);
		Log.e("AlarmManager", "enforce, before end: " + endTime);
		Log.e("AlarmManager", "enforce, before current: " + currentTime);


		if (startTime <= currentTime) {
			startTime += AlarmManager.INTERVAL_DAY;
		}
		if (endTime <= currentTime) {
			endTime += AlarmManager.INTERVAL_DAY;
		}
		if (endTime <= startTime) {
			endTime += AlarmManager.INTERVAL_DAY;
		}

		Log.e("AlarmManager", "enforce, after start: " + startTime);
		Log.e("AlarmManager", "enforce, after end: " + endTime);
		Log.e("AlarmManager", "enforce, after current: " + currentTime);


		PendingIntent startPI = getPendingIntent(StartTimeReceiver.class);
		PendingIntent endPI = getPendingIntent(EndTimeReceiver.class);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, startPI);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTime, endPI);

		Log.e("AlarmManager", "setHasReceiver: true");
	}

	private PendingIntent getPendingIntent(Class<? extends BroadcastReceiver> className) {
		Intent intent = new Intent(context, className);
//		intent.putExtra(TRIGGER_MILLISECONDS, triggerMilliseconds);

//		Log.e("AlarmManager", "putExtra(key: " + TRIGGER_MILLISECONDS + ", value: " + triggerMilliseconds + ")");

		return PendingIntent.getBroadcast(context, 1, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	//#endregion
	//#region cancel

	public void cancel() {

		cancel(StartTimeReceiver.class);
		cancel(EndTimeReceiver.class);
	}

	private void cancel(Class<? extends BroadcastReceiver> className) {

		Log.e("getCalendar", "cancel: " + className.getName());

		Intent intent = new Intent(context, className);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	//#endregion
}
