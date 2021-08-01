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

import static com.group4.khoatritoan.k_it.custom.MyKey.TRIGGER_MILLISECONDS;

public class AlarmManagerHelper {

	Context context;
	public AlarmManagerHelper(Context context) {
		this.context = context;
	}

	//#region enforce

	public void enforce(String startTimeWithoutSecond, String endTimeWithoutSecond) {

		Calendar startCalendar = getCalendar(startTimeWithoutSecond);
		Calendar endCalendar = getCalendar(endTimeWithoutSecond);

		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.set(Calendar.SECOND, 0);
		currentCalendar.set(Calendar.MILLISECOND, 0);


		Log.e("AlarmManager", "enforce, before start: " + startCalendar.getTimeInMillis());
		Log.e("AlarmManager", "enforce, before end: " + endCalendar.getTimeInMillis());

		if (startCalendar.before(currentCalendar) || startCalendar.equals(currentCalendar)) {
			startCalendar.add(Calendar.DATE, 1);
		}
		if (endCalendar.before(currentCalendar) || endCalendar.equals(currentCalendar)) {
			endCalendar.add(Calendar.DATE, 1);
		}
		if (startCalendar.after(endCalendar) || startCalendar.equals(endCalendar)) {
			endCalendar.add(Calendar.DATE, 1);
		}

		Log.e("AlarmManager", "enforce, after start: " + startCalendar.getTimeInMillis());
		Log.e("AlarmManager", "enforce, after end: " + endCalendar.getTimeInMillis());

		PendingIntent startPI = getPendingIntent(StartTimeReceiver.class,
				startCalendar.getTimeInMillis());
		PendingIntent endPI = getPendingIntent(EndTimeReceiver.class,
				endCalendar.getTimeInMillis());

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
				startCalendar.getTimeInMillis(), startPI);

		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
				endCalendar.getTimeInMillis(), endPI);

		Log.e("AlarmManager", "setHasReceiver: true");
		LocalRepository repository = new LocalRepository(context);
		repository.setHasReceiver(true);
	}

	private Calendar getCalendar(String timeWithoutSecond) {

		int hour =  Integer.parseInt(timeWithoutSecond.substring(0, 2));
		int minute = Integer.parseInt(timeWithoutSecond.substring(3, 5));

		Log.e("AlarmManager", "getCalendar: " + hour + ":" + minute);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	private PendingIntent getPendingIntent(Class<? extends BroadcastReceiver> className,
										   long triggerMilliseconds) {
		Intent intent = new Intent(context, className);
		intent.putExtra(TRIGGER_MILLISECONDS, triggerMilliseconds);

		Log.e("AlarmManager", "putExtra(key: " + TRIGGER_MILLISECONDS + ", value: " + triggerMilliseconds + ")");

		return PendingIntent.getBroadcast(context, 1, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	//#endregion
	//#region cancel

	public void cancel() {

		cancel(StartTimeReceiver.class);
		cancel(EndTimeReceiver.class);

		LocalRepository repository = new LocalRepository(context);
		repository.setHasReceiver(false);
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
