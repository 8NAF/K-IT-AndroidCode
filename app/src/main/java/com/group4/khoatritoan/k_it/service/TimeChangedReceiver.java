package com.group4.khoatritoan.k_it.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.group4.khoatritoan.k_it.repository.LocalRepository;

public class TimeChangedReceiver extends BroadcastReceiver {

	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {

		LocalRepository repository = new LocalRepository(context);
		if (!repository.getHasReceiver()) {
			return;
		}

		this.context = context;

		String actionName = intent.getAction();
		switch (actionName) {
			case Intent.ACTION_TIME_CHANGED:
				handleTimeChanged();
				break;

			case Intent.ACTION_TIMEZONE_CHANGED:
				handleTimeZoneChanged();
				break;

			case Intent.ACTION_DATE_CHANGED:
				handleDateChanged();
				break;
		}
	}


	private void handleTimeChanged() {

	}

	private void handleTimeZoneChanged() {

	}

	private void handleDateChanged() {

	}
}