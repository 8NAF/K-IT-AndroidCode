package com.group4.khoatritoan.k_it.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.repository.DatabasePath;
import com.group4.khoatritoan.k_it.repository.HomeRepository;

import static android.content.Context.MODE_PRIVATE;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.AUTO_MODE_GROUP;
import static com.group4.khoatritoan.k_it.repository.HomeRepository.IS_TURN_ON_MODE;

public class StartTimeReceiver extends BroadcastReceiver {

	private Context context;
	private Intent intent;
	private boolean isTurnOnMode;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;

		repeating();
		isTurnOnMode = HomeRepository.getInstance().getIsTurnOnMode(context).getValue();
		updateTurnOnNotificationToRepository();
		updateTurnOnNotificationToFirebase();
	}


	private void repeating() {
		long triggerMilliseconds = intent.getLongExtra("triggerMilliseconds", 0);
		triggerMilliseconds += AlarmManager.INTERVAL_DAY;

		Log.e("next", "start: " + triggerMilliseconds);

		Intent intent = new Intent(context, StartTimeReceiver.class);
		intent.putExtra("triggerMilliseconds", triggerMilliseconds);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
				triggerMilliseconds, pendingIntent);
	}

//	private boolean getIsTurnOnMode() {
//
//		SharedPreferences sp = context.getSharedPreferences(AUTO_MODE_GROUP, MODE_PRIVATE);
//		return sp.getBoolean(IS_TURN_ON_MODE, true);
//	}

	private void updateTurnOnNotificationToRepository() {
		HomeRepository.getInstance().setTurnOnNotification(context, isTurnOnMode);
	}

	private void updateTurnOnNotificationToFirebase() {

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(DatabasePath.TURN_ON_NOTIFICATION_PATH);
		ref.setValue(isTurnOnMode).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Log.e("start TON", "success: " + isTurnOnMode);
				notifyToClient();
			}
			else {
				Log.e("start TON", "error: " + task.getException());
			}
		});
	}

	private void notifyToClient() {

		String channelId = context.getString(R.string.project_id);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		String content = isTurnOnMode ? context.getString(R.string.label_turn_on) :
				context.getString(R.string.label_turn_off);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, channelId)
						.setSmallIcon(R.drawable.ic_notifications_on_24)
						.setContentTitle(context.getString(R.string.title_start_change_turn_on_notification))
						.setContentText(content)
						.setAutoCancel(true)
						.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
						.setSound(defaultSoundUri)
						.setDefaults(Notification.DEFAULT_ALL)
						.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
