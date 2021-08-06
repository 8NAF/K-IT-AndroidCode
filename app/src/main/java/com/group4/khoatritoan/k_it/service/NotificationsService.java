package com.group4.khoatritoan.k_it.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.repository.DatabasePath;
import com.group4.khoatritoan.k_it.custom.NotificationHelper;
import com.group4.khoatritoan.k_it.ui.main.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static androidx.core.app.NotificationCompat.VISIBILITY_PRIVATE;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.FCM_REGISTRATION_TOKEN_PATH;
import static com.group4.khoatritoan.k_it.repository.MyKey.LOG_ID;

public class NotificationsService extends FirebaseMessagingService {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onNewToken(@NonNull @NotNull String token) {
		super.onNewToken(token);
		savingTokenIntoDatabase(token);
		Log.e("token", token);
	}

	@Override
	public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			sendNotification(remoteMessage);
			responseToServer(remoteMessage.getData());
		}
	}

	private void responseToServer(Map<String, String> data) {

		if (data.containsKey(LOG_ID)) {
			String logId = data.get(LOG_ID);
			String isReceivedPath = DatabasePath.getIsReceived(logId);
			Log.e("isReceived path: ", isReceivedPath);
			FirebaseDatabase.getInstance().getReference(isReceivedPath).setValue(true);
		}
	}

	private void savingTokenIntoDatabase(String token) {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(FCM_REGISTRATION_TOKEN_PATH);
		ref.setValue(token);
	}

	private void sendNotification(RemoteMessage remoteMessage) {

		RemoteMessage.Notification firebaseNotification = remoteMessage.getNotification();

		String channelId = getString(R.string.project_id);
		Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ getPackageName() + "/" + R.raw.alert);

		PendingIntent pendingIntent = new NavDeepLinkBuilder(this)
				.setComponentName(MainActivity.class)
				.setGraph(R.navigation.bottom_navigation)
				.setDestination(R.id.navigation_log)
				.createPendingIntent();

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setSmallIcon(R.drawable.ic_notifications_on_24)
						.setContentTitle(firebaseNotification.getTitle())
						.setContentText(firebaseNotification.getBody())
						.setAutoCancel(true)
						.setContentIntent(pendingIntent)
						.setVisibility(VISIBILITY_PRIVATE)
						.setSound(soundUri)
						.setPriority(NotificationCompat.PRIORITY_MAX)
						.setGroup(remoteMessage.getCollapseKey());
//						.addAction(new NotificationCompat.Action(
//								android.R.drawable.sym_call_missed,
//								"Cancel",
//								PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
//						.addAction(new NotificationCompat.Action(
//								android.R.drawable.sym_call_outgoing,
//								"OK",
//								PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

		NotificationManager notificationManager = (NotificationManager)
				getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = NotificationHelper.createChannel(
					channelId, NotificationManager.IMPORTANCE_HIGH, soundUri
			);
			notificationManager.createNotificationChannel(channel);
		}

		Notification notification = notificationBuilder.build();
		notification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}
}