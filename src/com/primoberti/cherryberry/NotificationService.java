/*
 * Copyright 2012 Alberto Salmerón Moreno
 * 
 * This file is part of CherryBerry - https://github.com/berti/CherryBerry.
 * 
 * “Pomodoro Technique® is a registered trademark of Francesco Cirillo. This
 * application is not affiliated by, associated with nor endorsed by the
 * Pomodoro Technique® or Francesco Cirillo.
 * 
 * CherryBerry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CherryBerry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CherryBerry.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.primoberti.cherryberry;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;

/**
 * Service for displaying notifications related to a pomodoro.
 * 
 * @author berti
 */
public class NotificationService extends Service {

	/* Public constants ************************ */

	public final static int NOTIFICATION_ID = 1;

	/* Public methods ************************** */

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction().equals(PomodoroTimerService.POMODORO_FINISHED)) {
			showPomodoroNotification();
		}
		else if (intent.getAction().equals(PomodoroTimerService.BREAK_FINISHED)) {
			showBreakNotification();
		}

		stopSelf();

		return START_NOT_STICKY;
	}

	/* Private methods ************************* */

	private void showPomodoroNotification() {
		showNotification(NOTIFICATION_ID,
				R.string.notification_title_pomodoro_finished,
				R.string.app_name,
				R.string.notification_text_pomodoro_finished);
	}

	private void showBreakNotification() {
		showNotification(NOTIFICATION_ID,
				R.string.notification_title_break_finished, R.string.app_name,
				R.string.notification_text_break_finished);
	}

	private void showNotification(int id, int tickerText, int contentTitle,
			int contentText) {
		Resources resources = getResources();

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.ic_stat_generic;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon,
				resources.getString(tickerText), when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		if (PreferencesHelper.isNotificationVibration(this)) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		if (PreferencesHelper.isNotificationSound(this)) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}

		if (PreferencesHelper.isNotificationLight(this)) {
			notification.ledARGB = 0xffd60707;
			notification.ledOnMS = 300;
			notification.ledOffMS = 3000;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		}

		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(this, CherryBerryActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context,
				resources.getString(contentTitle),
				resources.getString(contentText), contentIntent);

		mNotificationManager.notify(id, notification);
	}

}
