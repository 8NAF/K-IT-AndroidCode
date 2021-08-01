package com.group4.khoatritoan.k_it.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.model.HomeModel;

import static com.group4.khoatritoan.k_it.custom.MyKey.TRIGGER_MILLISECONDS;

public class EndTimeReceiver extends BroadcastReceiver {

	private Context context;
	private Intent intent;
	private HomeModel model;
	private boolean isTurnOffMode;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;
		this.model = new HomeModel(context);

		repeating();
		isTurnOffMode = ! model.getIsTurnOnMode().getValue();
		updateTurnOnNotification();
	}

	private void repeating() {
		long triggerMilliseconds = intent.getLongExtra(TRIGGER_MILLISECONDS, 0);
		triggerMilliseconds += AlarmManager.INTERVAL_DAY;

		Log.e("AlarmManager", "next end at: " + triggerMilliseconds);

		Intent intent = new Intent(context, EndTimeReceiver.class);

		intent.putExtra(TRIGGER_MILLISECONDS, triggerMilliseconds);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
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

		String channelId = context.getString(R.string.project_id);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		String content = isTurnOffMode ?
				context.getString(R.string.label_turn_on) :
				context.getString(R.string.label_turn_off);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, channelId)
						.setSmallIcon(R.drawable.ic_notifications_on_24)
						.setContentTitle(context.getString(R.string.title_end_change_turn_on_notification))
						.setContentText(content)
						.setAutoCancel(true)
						.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
						.setSound(defaultSoundUri)
						.setDefaults(Notification.DEFAULT_ALL)
						.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					channelId,
					"Default channel",
					NotificationManager.IMPORTANCE_DEFAULT);

			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(1, notificationBuilder.build());
	}
}
