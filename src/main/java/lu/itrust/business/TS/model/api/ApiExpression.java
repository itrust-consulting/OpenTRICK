package lu.itrust.business.TS.model.api;

/**
 * Represents the body of an API request for evaluating an expression.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class ApiExpression {
	/** The expression to parse. */
	private String expression;
	/**
	 * The maximum age (in seconds) of notifications that shall be considered in the frequency computation.
	 */
	private long timespan;
	/**
	 * The duration (in seconds) of the time unit in the definition of the frequency.
	 * For example, say the unit duration is set to 3600 seconds (1 hour), then a
	 * frequency value of 3.54 in interpreted as 3.54 events per _hour_.   
	 */
	private long unitDuration;
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public long getTimespan() {
		return timespan;
	}
	public void setTimespan(long timespan) {
		this.timespan = timespan;
	}
	public long getUnitDuration() {
		return unitDuration;
	}
	public void setUnitDuration(long unitDuration) {
		this.unitDuration = unitDuration;
	}
}
