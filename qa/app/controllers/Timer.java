package controllers;

import java.util.Date;

/**
 * A utility class to help us run benchmarks.
 * 
 * @author Oscar.Nierstrasz@acm.org
 * @version 1.0 1998-11-25
 */
public class Timer {
	long _startTime;

	/**
	 * You can either create a new instance whenever you want to time something,
	 * or you can reset() an existing instance.
	 */
	public Timer() {
		this.reset();
	}

	public void reset() {
		_startTime = this.timeNow();
	}

	/**
	 * How many milliseconds have elapsed since the last reset()? NB: does not
	 * reset the timer!
	 */
	public long timeElapsed() {
		return this.timeNow() - _startTime;
	}

	protected long timeNow() {
		return new Date().getTime();
	}
}
