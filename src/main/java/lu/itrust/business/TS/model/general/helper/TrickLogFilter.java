/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;

/**
 * @author eomar
 *
 */
public class TrickLogFilter extends TrickFilter {

	private LogType type;
	
	private LogLevel level;

	/**
	 * 
	 */
	public TrickLogFilter() {
	}

	/**
	 * @param direction
	 * @param size
	 * @param type
	 * @param level
	 */
	public TrickLogFilter(LogType type, LogLevel level,String direction, int size) {
		super(direction, size);
		this.type = type;
		this.level = level;
	}

	public TrickLogFilter(Integer size, String level, String type, String direction) {
		super(CheckDirection(direction)? direction : "asc", size == null || size<60? 60:  size);
		setLevel(TryParseLevel(level));
		setType(TryParseType(type));
	}

	private LogType TryParseType(String type) {
		try {
			return type == null? null : LogType.valueOf(type);
		} catch (Exception e) {
			return null;
		}
	}

	private LogLevel TryParseLevel(String level) {
		try {
			return level== null? null : LogLevel.valueOf(level);
		} catch (Exception e) {
			return null;
		}
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel level) {
		this.level = level;
	}

	public LogType getType() {
		return type;
	}

	public void setType(LogType type) {
		this.type = type;
	}
	
	public boolean isOrderDescending() {
		return getDirection()!=null && getDirection().equalsIgnoreCase("desc");
	}
	
}
