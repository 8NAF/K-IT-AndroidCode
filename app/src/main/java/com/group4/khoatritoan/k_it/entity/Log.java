package com.group4.khoatritoan.k_it.entity;

import java.util.Objects;

public class Log {
	
	public long start;
	public long end;
	public boolean isReceived;

	public Log() {}

	public Log(long start, long end, boolean isReceived) {
		this.start = start;
		this.end = end;
		this.isReceived = isReceived;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Log)) return false;
		Log log = (Log) o;
		return start == log.start &&
				end == log.end &&
				isReceived == log.isReceived;
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, end, isReceived);
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
