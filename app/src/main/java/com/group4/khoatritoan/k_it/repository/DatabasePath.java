package com.group4.khoatritoan.k_it.repository;

public class DatabasePath {

	// Cấp 1
	public final static String LOGS_PATH = "/logs";
	public final static String APPLICATION_CONFIGURATION_PATH = "/applicationConfiguration";

	//Cấp 2
	public final static String FCM_REGISTRATION_TOKEN_PATH = APPLICATION_CONFIGURATION_PATH + "/FCMRegistrationToken";
	public final static String TURN_ON_NOTIFICATION_PATH = APPLICATION_CONFIGURATION_PATH + "/turnOnNotification";
	public final static String DELAY_NOTIFICATION_SECONDS_PATH = APPLICATION_CONFIGURATION_PATH + "/delayNotificationSeconds";
	public final static String MAX_DELAY_SECONDS_PATH = APPLICATION_CONFIGURATION_PATH + "/maxDelaySeconds";
	public final static String MIN_DELAY_SECONDS_PATH = APPLICATION_CONFIGURATION_PATH + "/minDelaySeconds";

}
