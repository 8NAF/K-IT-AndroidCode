package com.group4.khoatritoan.k_it.repository;

public class DatabasePath {

	//#region Cấp 1
	public final static String LOGS_PATH = "/logs";
	public final static String APPLICATION_CONFIGURATION_PATH = "/applicationConfiguration";
	//#endregion

	//#region Cấp 2
	public final static String FCM_REGISTRATION_TOKEN_PATH = APPLICATION_CONFIGURATION_PATH + "/FCMRegistrationToken";
	public final static String TURN_ON_NOTIFICATION_PATH = APPLICATION_CONFIGURATION_PATH + "/turnOnNotification";
	public final static String DELAY_NOTIFICATION_SECONDS_PATH = APPLICATION_CONFIGURATION_PATH + "/delayNotificationSeconds";
	public final static String MAX_DELAY_SECONDS_PATH = APPLICATION_CONFIGURATION_PATH + "/maxDelaySeconds";
	public final static String MIN_DELAY_SECONDS_PATH = APPLICATION_CONFIGURATION_PATH + "/minDelaySeconds";
	//#endregion

	//#region Cấp 3
	public final static String IS_RECEIVED = "isReceived";
	public static String getIsReceived(String logId) {
		return LOGS_PATH + "/" + logId + "/" + IS_RECEIVED;
	}

	public final static String START = "start";
	public static String getStart(String logId) {
		return LOGS_PATH + "/" + logId + "/" + START;
	}

	public final static String END = "end";
	public static String getEnd(String logId) {
		return LOGS_PATH + "/" + logId + "/" + END;
	}
	//#endregion
}