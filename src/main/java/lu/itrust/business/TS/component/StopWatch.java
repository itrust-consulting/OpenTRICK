package lu.itrust.business.TS.component;

/**
 * Class which measures code execution time.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Aug 25, 2015
 */
public class StopWatch {
	/**
	 * The total measured time, in nanoseconds.
	 */
	private long totalNanoTime = 0;

	/**
	 * The time stamp which the watch has been running since, or null if it is
	 * not running. The value is the output of System.nanoTime() at the time of
	 * starting.
	 */
	private Long startTime = null;

	/**
	 * If true, the {@link #totalTime} variable is reset to 0 at the next call
	 * of {@link #start()}.
	 */
	private boolean shallResetAtStart = false;

	/**
	 * Starts or resumes the stop watch.
	 * 
	 * @throws IllegalStateException
	 *             Throws an exception if the stop watch is already running.
	 */
	public void start() throws IllegalStateException {
		if (startTime != null)
			throw new IllegalStateException("Stop watch already started");

		if (shallResetAtStart)
			totalNanoTime = 0;
		startTime = System.nanoTime();
	}

	/**
	 * Suspends the stop watch, adding the time passed since the call of
	 * {@link #start()} to the overall time measured.
	 * 
	 * @throws IllegalStateException
	 *             Throws an exception if the stop watch is not running.
	 */
	public void suspend() throws IllegalStateException {
		if (startTime == null)
			throw new IllegalStateException("Stop watch has not been started");

		totalNanoTime += System.nanoTime() - startTime;
		startTime = null;
	}

	/**
	 * Stops the stop watch, adding the time passed since the call of
	 * {@link #start()} to the overall time measured. Works just like
	 * {@link #suspend()}, but in addition resets the stop watch at the next
	 * call of {@link #start()}.
	 * 
	 * @throws IllegalStateException
	 *             Throws an exception if the stop watch is not running.
	 */
	public void stop() throws IllegalStateException {
		suspend();
		shallResetAtStart = true;
	}

	/**
	 * Gets the number of nanoseconds measured by the stop watch, summing over
	 * all measuring time intervals since the last call to {@link #stop()}.
	 */
	public long getElapsedNanoseconds() {
		return totalNanoTime;
	}

	/**
	 * Gets the number of milliseconds measured by the stop watch, summing over
	 * all measuring time intervals since the last call to {@link #stop()}.
	 * 
	 * Equals the integer part of {@link #getElapsedNanoseconds()} / 1000.
	 */
	public long getElapsedMilliseconds() {
		return totalNanoTime / 1000000; // floor
	}

	/**
	 * Gets the number of seconds measured by the stop watch, summing over all
	 * measuring time intervals since the last call to {@link #stop()}.
	 * 
	 * Equals {@link #getElapsedNanoseconds()} / 1E9.
	 */
	public double getElapsedSeconds() {
		return totalNanoTime / 1E9;
	}
}
