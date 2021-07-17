package com.group4.khoatritoan.k_it.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.repository.DatabasePath;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static androidx.core.app.NotificationCompat.VISIBILITY_PRIVATE;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.FCM_REGISTRATION_TOKEN_PATH;
import static com.group4.khoatritoan.k_it.repository.DatabasePath.LOGS_PATH;

public class NotificationsService extends FirebaseMessagingService {

	@Override
	public void onCreate() {
		super.onCreate();

		Log.e("NotificationsService", "start");
	}

	@Override
	public void onNewToken(@NonNull @NotNull String token) {
		super.onNewToken(token);
		sendRegistrationToServer(token);
		Log.e("token", token);
	}

	@Override
	public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		Log.d("token", "Message Notification Body: " + remoteMessage.getNotification().getBody());
		sendNotification(remoteMessage);
		responseToServer(remoteMessage.getData());
	}

	private void responseToServer(Map<String, String> data) {

		if (data.containsKey("notificationId")) {
			String notificationId = data.get("notificationId");
			String isReceivedPath = LOGS_PATH + "/" + notificationId + "/" + "isReceived";
			Log.e("isReceived path: ", isReceivedPath);
			FirebaseDatabase.getInstance().getReference(isReceivedPath).setValue(true);
		}
	}

	private void sendRegistrationToServer(String token) {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(FCM_REGISTRATION_TOKEN_PATH);
		ref.setValue(token);
	}

	private void sendNotification(RemoteMessage remoteMessage) {

		RemoteMessage.Notification firebaseNotification = remoteMessage.getNotification();

		String channelId = getString(R.string.project_id);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setSmallIcon(R.drawable.ic_notifications_on_24)
						.setContentTitle(firebaseNotification.getTitle())
						.setContentText(firebaseNotification.getBody())
						.setAutoCancel(true)
						.setGroup(remoteMessage.getCollapseKey())
						.setVisibility(VISIBILITY_PRIVATE)
						.setSound(defaultSoundUri)
						.setDefaults(Notification.DEFAULT_ALL)
						.setPriority(NotificationCompat.PRIORITY_MAX);
//						.addAction(new NotificationCompat.Action(
//								android.R.drawable.sym_call_missed,
//								"Cancel",
//								PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
//						.addAction(new NotificationCompat.Action(
//								android.R.drawable.sym_call_outgoing,
//								"OK",
//								PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					channelId,
					"Default channel",
					NotificationManager.IMPORTANCE_DEFAULT);

			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0, notificationBuilder.build());
	}
}
