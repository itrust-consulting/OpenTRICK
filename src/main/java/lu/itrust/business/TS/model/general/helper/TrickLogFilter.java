/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;

/**
 * @author eomar
 *
 */
public class TrickLogFilter extends TrickFilter {

	private LogType type;
	
	private LogLevel level;
	
	private LogAction action;
	
	private String author;

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
	public TrickLogFilter(LogType type, LogLevel level, LogAction action, String author,String direction, int size) {
		super(direction, size);
		setType(type);
		setLevel(level);
		setAction(action);
		setAuthor(author);
	}

	public TrickLogFilter(Integer size, String level, String type,String action, String author, String direction) {
		super(CheckDirection(direction)? direction : "asc", size == null || size < 60 ? 60:  size);
		setLevel(TryParseLevel(level));
		setType(TryParseType(type));
		setAction(TryParse(action));
		setAuthor(author);
	}

	private LogAction TryParse(String action) {
		try {
			return action == null? null : LogAction.valueOf(action);
		} catch (Exception e) {
			return null;
		}
		
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

	public LogAction getAction() {
		return action;
	}

	public void setAction(LogAction action) {
		this.action = action;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "TrickLogFilter [type=" + type + ", level=" + level + ", action=" + action + ", author=" + author + ", size=" + getSize() + ", direction="
				+ getDirection() + "]";
	}

	
	
	
}
