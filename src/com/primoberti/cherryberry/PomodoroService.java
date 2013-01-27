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

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.primoberti.cherryberry.Session.BreakType;
import com.primoberti.cherryberry.Session.Status;

/**
 * Timer-related functionality to control a pomodoro.
 * 
 * @author berti
 */
public class PomodoroService extends Service implements
		PomodoroServiceInterface {

	/* Public constants ************************ */

	public final static String BREAK_FINISHED = "com.primoberti.cherryberry.BREAK_FINISHED";

	public final static String POMODORO_FINISHED = "com.primoberti.cherryberry.POMODORO_FINISHED";

	public final static int NOTIFICATION_ID = 1;

	/* Private constants *********************** */

	private final static String SHARED_PREFS = PomodoroService.class
			+ "_SHARED_PREFS";

	/* Private instance constants ************** */

	private final IdleState idleState = new IdleState();
	private final PomodoroRunningState pomodoroRunningState = new PomodoroRunningState();
	private final PomodoroFinishedState pomodoroFinishedState = new PomodoroFinishedState();
	private final BreakRunningState breakRunningState = new BreakRunningState();

	/* Private fields ************************** */

	private SessionImpl session;

	private PomodoroListener listener;

	private State state = idleState;

	private IBinder binder = new LocalBinder();

	/* Public methods ************************** */

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("PomodoroTimerService", "onBind");

		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		timeout();

		stopSelf();

		return START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		Log.d("PomodoroTimerService", "onCreate");

		session = new SessionImpl();

		restoreState();
	}

	@Override
	public void onDestroy() {
		Log.d("PomodoroTimerService", "onDestroy");

		super.onDestroy();
	}

	public PomodoroListener getListener() {
		return listener;
	}

	/* PomodoroServiceInterface methods ******** */

	public Session getSession() {
		return session;
	}

	/**
	 * Start a new pomodoro from idle, or break after finishing a pomodoro.
	 */
	@Override
	public void start() {
		state = state.start();
	}

	/**
	 * Cancels the current pomodoro or break and returns to idle.
	 */
	@Override
	public void cancel() {
		state = state.cancel();
	}

	@Override
	public void setListener(PomodoroListener listener) {
		this.listener = listener;
	}

	/* Private methods ************************* */

	private void timeout() {
		state = state.timeout();
	}

	/**
	 * Update the status, start and finish times of the current session. Start
	 * time is set to the current time, while finish time is computed from that
	 * and the given duration. In addition, save the state.
	 * 
	 * @param newStatus the new status of the session
	 * @param millis the duration of this new status
	 */
	private void updateSession(Status newStatus, long millis) {
		session.setStatus(newStatus);
		session.setStartTime(System.currentTimeMillis());
		session.setFinishTime(session.getStartTime() + millis);

		saveState();
	}

	/**
	 * Update the status of the current session, and sets it start and finish
	 * time to the current time. This is for status that don't have a defined
	 * duration. In addition, save the state.
	 * 
	 * @param newStatus the new status of the session
	 */
	private void updateSession(Status newStatus) {
		updateSession(newStatus, 0);
	}

	/* Private state saving/restoring methods ** */

	private void saveState() {
		Log.d("PomodoroTimerService", "saveState " + session.getStatus());

		SharedPreferences preferences = getSharedPreferences(SHARED_PREFS,
				MODE_PRIVATE);

		session.save(preferences);
	}

	private void restoreState() {
		SharedPreferences preferences = getSharedPreferences(SHARED_PREFS,
				MODE_PRIVATE);

		session = SessionImpl.restore(preferences);
		restore(session);

		Log.d("PomodoroTimerService", "restoreState " + session.getStatus());

		if (session.getStatus() == Status.POMODORO_RUNNING) {
			if (session.getFinishTime() <= System.currentTimeMillis()) {
				updateSession(Status.POMODORO_FINISHED);
			}
		}
		else if (session.getStatus() == Status.BREAK_RUNNING) {
			if (session.getFinishTime() <= System.currentTimeMillis()) {
				updateSession(Status.BREAK_FINISHED);
			}
		}
	}

	private void restore(Session session) {
		switch (session.getStatus()) {
		case IDLE:
		case BREAK_FINISHED:
			state = idleState;
			break;
		case POMODORO_RUNNING:
			state = pomodoroRunningState;
			break;
		case POMODORO_FINISHED:
			state = pomodoroFinishedState;
			break;
		case BREAK_RUNNING:
			state = breakRunningState;
			break;
		}
	}

	/* Public inner classes ******************** */

	public class LocalBinder extends Binder {

		PomodoroServiceInterface getService() {
			return PomodoroService.this;
		}

	}

	/* Private inner classes ******************* */

	private abstract class State {

		public State start() {
			return this;
		}

		public State cancel() {
			return this;
		}

		public State timeout() {
			return this;
		}

	}

	private class IdleState extends State {

		@Override
		public State start() {
			long millis = PreferencesHelper
					.getPomodoroDuration(PomodoroService.this);

			updateSession(Status.POMODORO_RUNNING, millis);

			AlarmHelper.setPomodoroAlarm(PomodoroService.this, millis);
			NotificationHelper.showPersistentPomodoroNotification(
					PomodoroService.this, millis);

			if (listener != null) {
				listener.onPomodoroStart(PomodoroService.this);
			}

			return pomodoroRunningState;
		}

	}

	private class PomodoroRunningState extends State {

		@Override
		public State cancel() {
			updateSession(Status.IDLE);

			AlarmHelper.cancelPomodoroAlarm(PomodoroService.this);
			NotificationHelper.hidePersistentNotification(PomodoroService.this);

			if (listener != null) {
				listener.onPomodoroCancel(PomodoroService.this);
			}

			return idleState;
		}

		@Override
		public State timeout() {
			session.incrementCount();

			int interval = PreferencesHelper
					.getLongBreakInterval(PomodoroService.this);
			if (session.getCount() % interval == 0) {
				session.setBreakType(BreakType.LONG);
			}
			else {
				session.setBreakType(BreakType.NORMAL);
			}

			updateSession(Status.POMODORO_FINISHED);

			NotificationHelper.showPomodoroNotification(PomodoroService.this);

			if (listener != null) {
				listener.onPomodoroFinish(PomodoroService.this);
			}

			return pomodoroFinishedState;
		}

	}

	private class PomodoroFinishedState extends State {

		@Override
		public State start() {
			long millis = PreferencesHelper.getBreakDuration(
					PomodoroService.this, session.getBreakType());

			updateSession(Status.BREAK_RUNNING, millis);

			AlarmHelper.setBreakAlarm(PomodoroService.this, millis);
			NotificationHelper.showPersistentBreakNotification(
					PomodoroService.this, millis);

			if (listener != null) {
				listener.onBreakStart(PomodoroService.this);
			}

			return breakRunningState;
		}

		@Override
		public State cancel() {
			updateSession(Status.IDLE);

			NotificationHelper.hidePersistentNotification(PomodoroService.this);

			if (listener != null) {
				listener.onBreakCancel(PomodoroService.this);
			}

			return idleState;
		}

	}

	private class BreakRunningState extends State {

		@Override
		public State cancel() {
			updateSession(Status.IDLE);

			AlarmHelper.cancelBreakAlarm(PomodoroService.this);
			NotificationHelper.hidePersistentNotification(PomodoroService.this);

			if (listener != null) {
				listener.onBreakCancel(PomodoroService.this);
			}

			return idleState;
		}

		@Override
		public State timeout() {
			updateSession(Status.IDLE);

			NotificationHelper.showBreakNotification(PomodoroService.this);

			if (listener != null) {
				listener.onBreakFinish(PomodoroService.this);
			}

			return idleState;
		}

	}

}
