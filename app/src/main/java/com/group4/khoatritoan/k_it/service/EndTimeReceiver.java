package com.group4.khoatritoan.k_it.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.NotificationHelper;
import com.group4.khoatritoan.k_it.model.HomeModel;


public class EndTimeReceiver extends BroadcastReceiver {

	private Context context;
	private HomeModel model;
	private boolean isTurnOffMode;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.model = new HomeModel(context);
		this.isTurnOffMode = ! model.getIsTurnOnMode().getValue();

		repeating();
		updateTurnOnNotification();
	}

	private void repeating() {

		long triggerMilliseconds = model.getEndTime().getValue();
		triggerMilliseconds += AlarmManager.INTERVAL_DAY;
		model.setEndTime(triggerMilliseconds);

		Log.e("AlarmManager", "next end at: " + triggerMilliseconds);

		Intent intent = new Intent(context, EndTimeReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
				triggerMilliseconds, pendingIntent);
	}

	private void updateTurnOnNotification() {
		model.setTurnOnNotification(isTurnOffMode, task -> {
			if (task.isSuccessful()) {
				Log.e("AlarmManager", "end - set turnOnNotification success: " + isTurnOffMode);
				notifyToClient();
			}
			else {
				Log.e("AlarmManager", "end - set turnOnNotification error: " + task.getException());
			}
		});
	}

	private void notifyToClient() {

		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager == null) {
			return;
		}

		String channelId = context.getString(R.string.project_id);
		Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ context.getPackageName() + "/" + R.raw.info);
		String contentText = isTurnOffMode ?
				context.getString(R.string.label_turn_on):
				context.getString(R.string.label_turn_off);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, channelId)
						.setSmallIcon(R.drawable.ic_notifications_on_24)
						.setContentTitle(context.getString(R.string.title_end_change_turn_on_notification))
						.setContentText(contentText)
						.setAutoCancel(true)
						.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
						.setSound(soundUri)
						.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = NotificationHelper.createChannel(
					channelId, NotificationManager.IMPORTANCE_DEFAULT, soundUri
			);
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(1, notificationBuilder.build());
	}
}