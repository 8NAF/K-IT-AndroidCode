package com.group4.khoatritoan.k_it.service;

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
import com.group4.khoatritoan.k_it.custom.AlarmManagerHelper;
import com.group4.khoatritoan.k_it.custom.NotificationHelper;
import com.group4.khoatritoan.k_it.model.HomeModel;
import com.group4.khoatritoan.k_it.ui.main.MainActivity;

import java.util.HashSet;
import java.util.Set;

public abstract class TimeReceiver extends BroadcastReceiver {

	protected Context context;
	protected HomeModel model;
	protected boolean isTurnOnMode;

	private static final Set<String> TIME_ACTIONS;
	static {
		TIME_ACTIONS = new HashSet<>();
		TIME_ACTIONS.add(Intent.ACTION_TIME_CHANGED);
		TIME_ACTIONS.add(Intent.ACTION_TIMEZONE_CHANGED);
		TIME_ACTIONS.add(Intent.ACTION_DATE_CHANGED);
	}

	@Override
	final public void onReceive(Context context, Intent intent) {

		this.context = context;
		this.model = new HomeModel(context);
		this.isTurnOnMode = model.getIsTurnOnMode().getValue();

		if (isTimeChanged(intent.getAction())) {
			return;
		}

		Log.e("AlarmManager", "action: " + intent.getAction());

		if (model.getHasReceiver()) {
			repeating();
			updateTurnOnNotification();
		}
	}

	private boolean isTimeChanged(String action) {

		if (TIME_ACTIONS.contains(action)) {

			if (!model.getHasReceiver()) {
				return true;
			}

			Log.e("AlarmManager", "action: " + action);

			String contentTitle = context
					.getString(R.string.title_auto_mode_is_canceled);
			String contentText = context.getString(R.string.label_time_changed);
			notifyToClient(contentTitle, contentText);

			model.setIsAutoModeEnabled(false);

			AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper(context);
			alarmManagerHelper.cancel();

			return true;
		}

		return false;
	}

	protected abstract void repeating();
	protected abstract void updateTurnOnNotification();

	protected void notifyToClient(String contentTitle, String contentText) {

		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (notificationManager == null) {
			return;
		}

		String channelId = context.getString(R.string.project_id);
		Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ context.getPackageName() + "/" + R.raw.info);
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, channelId)
						.setSmallIcon(R.drawable.ic_notifications_on_24)
						.setContentTitle(contentTitle)
						.setContentText(contentText)
						.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
						.setAutoCancel(true)
						.setContentIntent(pendingIntent)
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