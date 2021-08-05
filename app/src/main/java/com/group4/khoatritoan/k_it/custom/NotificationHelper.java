package com.group4.khoatritoan.k_it.custom;

import android.app.NotificationChannel;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;


public class NotificationHelper {

	@RequiresApi(api = Build.VERSION_CODES.O)
	public static NotificationChannel createChannel(String id, int property, Uri soundUri) {

		NotificationChannel channel = new NotificationChannel(
				id, "Default channel", property
		);

		AudioAttributes attributes = new AudioAttributes.Builder()
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.setUsage(AudioAttributes.USAGE_NOTIFICATION)
				.build();
		channel.setSound(soundUri, attributes);

		return channel;
	}
}