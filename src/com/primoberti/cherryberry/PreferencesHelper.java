/*
 * Copyright 2012 Alberto Salmerón Moreno
 * 
 * This file is part of CherryBerry - https://github.com/berti/CherryBerry.
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

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Abstract helper class for accessing shared preferences across CherryBerry.
 * 
 * @author berti
 */
public abstract class PreferencesHelper {

	/* Public static methods ******************* */

	public static long getPomodoroDuration(Context context) {
		int mins = getIntFromString(context,
				R.string.settings_key_pomodoro_duration, 25);
		return mins * 60 * 1000;
	}

	public static long getBreakDuration(Context context) {
		int mins = getIntFromString(context,
				R.string.settings_key_break_duration, 5);
		return mins * 60 * 1000;
	}

	public static long getLongBreakDuration(Context context) {
		int mins = getIntFromString(context,
				R.string.settings_key_long_break_duration, 15);
		return mins * 60 * 1000;
	}

	public static boolean isNotificationLight(Context context) {
		return getBoolean(context, R.string.settings_key_notification_light,
				true);
	}

	public static boolean isNotificationVibration(Context context) {
		return getBoolean(context,
				R.string.settings_key_notification_vibration, true);
	}

	public static Ringtone getNotificationRingtone(Context context) {
		Ringtone ringtone = null;

		Uri ringtoneUri = getNotificationRingtoneUri(context);
		if (ringtoneUri != null) {
			ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
		}

		return ringtone;
	}

	public static Uri getNotificationRingtoneUri(Context context) {
		Uri ringtoneUri = null;

		String ringtoneString = getString(context,
				R.string.settings_key_notification_ringtone, null);
		if (ringtoneString != null) {
			ringtoneUri = Uri.parse(ringtoneString);
		}

		return ringtoneUri;
	}

	/* Private static methods ****************** */

	private static int getInt(Context context, int key, int defValue) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getInt(context.getResources().getString(key),
				defValue);
	}

	private static int getIntFromString(Context context, int key, int defValue) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String stringKey = context.getResources().getString(key);
		String stringValue = preferences.getString(stringKey, null);
		return stringValue != null ? Integer.parseInt(stringValue) : defValue;
	}

	private static boolean getBoolean(Context context, int key, boolean defValue) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getBoolean(context.getResources().getString(key),
				defValue);
	}

	private static String getString(Context context, int key, String defValue) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getString(context.getResources().getString(key),
				defValue);
	}

}
