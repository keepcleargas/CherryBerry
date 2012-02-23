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
import android.preference.PreferenceManager;

/**
 * Abstract helper class for accessing shared preferences across CherryBerry.
 * 
 * @author berti
 */
public abstract class PreferencesHelper {

	/* Public static methods ******************* */

	public static long getPomodoroDuration(Context context) {
		int secs = getInt(context, R.string.settings_key_pomodoro_duration, 25);
		return secs * 1000;
	}

	public static long getBreakDuration(Context context) {
		int secs = getInt(context, R.string.settings_key_break_duration, 5);
		return secs * 1000;
	}

	public static long getLongBreakDuration(Context context) {
		int secs = getInt(context, R.string.settings_key_long_break_duration,
				15);
		return secs * 1000;
	}

	/* Private static methods ****************** */

	private static int getInt(Context context, int key, int defValue) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getInt(context.getResources().getString(key),
				defValue);
	}

}
