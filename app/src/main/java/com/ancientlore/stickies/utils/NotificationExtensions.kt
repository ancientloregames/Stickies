package com.ancientlore.stickies.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi

fun Context.getNotificationManager() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.getAlarmManager() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

fun Context.createNotification(id: Int, notification: Notification) = getNotificationManager().notify(id, notification)

fun Context.cancelNotification(id: Int) = getNotificationManager().cancel(id)

fun Context.scheduleAlarm(time: Long, operation: PendingIntent) {
	getAlarmManager().schedule(AlarmManager.RTC_WAKEUP, time, operation)
}

fun <T: BroadcastReceiver> Context.cancelAlarm(id: Int, clazz: Class<T>) {
	val intent = Intent(this, clazz)
	val pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
	getAlarmManager().cancel(pendingIntent)
}

fun AlarmManager.schedule(type: Int, time: Long, operation: PendingIntent) {
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		set(type, time, operation)
	else setExact(type, time, operation)
}

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.createChannel(id: String, title: String, importance: Int): NotificationChannel {
	val channel = NotificationChannel(id, title, importance)
	createNotificationChannel(channel)
	return channel
}