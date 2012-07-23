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

/**
 * A listener interface for {@link PomodoroService} events.
 * 
 * @author berti
 */
public interface PomodoroListener {
	
	public void onPomodoroStart(PomodoroService service);

	public void onPomodoroFinish(PomodoroService service);
	
	public void onPomodoroCancel(PomodoroService service);
	
	public void onBreakStart(PomodoroService service);

	public void onBreakFinish(PomodoroService service);
	
	public void onBreakCancel(PomodoroService service);

}