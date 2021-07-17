package com.group4.khoatritoan.k_it.model;

public class NotificationModel {
	
	public long start;
	public long end;
	public boolean isReceived;

	public NotificationModel() {}

	public NotificationModel(long start, long end, boolean isReceived) {
		this.start = start;
		this.end = end;
		this.isReceived = isReceived;
	}

	@Override
	public String toString() {
		return "NotificationModel{" +
				"start=" + start +
				", end=" + end +
				", isReceived=" + isReceived +
				'}';
	}
}
