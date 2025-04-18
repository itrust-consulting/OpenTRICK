package lu.itrust.business.ts.model.general.helper;

import java.io.Serializable;

import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;

/**
 * The TrickLogFilter class is a subclass of TrickFilter and implements the Serializable interface.
 * It represents a filter for logging information in the TrickService application.
 * 
 * The TrickLogFilter class provides methods to set and retrieve the log type, log level, log action, and author.
 * It also provides methods to check if the order is descending and to convert the filter to a string representation.
 * 
 * Example usage:
 * TrickLogFilter filter = new TrickLogFilter(LogType.ERROR, LogLevel.DEBUG, LogAction.CREATE, "John Doe", "asc", 100);
 * filter.setAuthor("Jane Smith");
 * LogLevel level = filter.getLevel();
 * 
 * Note: This class assumes the existence of the TrickFilter, LogType, LogLevel, and LogAction classes.
 */
public class TrickLogFilter extends TrickFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private LogType type;
	
	private LogLevel level;
	
	private LogAction action;
	
	private String author;

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

	/**
	 * Constructs a new TrickLogFilter object with the specified parameters.
	 *
	 * @param size      The maximum number of log entries to retrieve.
	 * @param level     The log level to filter by.
	 * @param type      The log type to filter by.
	 * @param action    The log action to filter by.
	 * @param author    The author of the log entries to filter by.
	 * @param direction The sort direction of the log entries.
	 */
	public TrickLogFilter(Integer size, String level, String type,String action, String author, String direction) {
		super(CheckDirection(direction)? direction : "asc", size == null || size < 60 ? 60:  size);
		setLevel(TryParseLevel(level));
		setType(TryParseType(type));
		setAction(TryParse(action));
		setAuthor(author);
	}

	/**
	 * Represents the possible actions for logging.
	 */
	private LogAction TryParse(String action) {
		try {
			return action == null? null : LogAction.valueOf(action);
		} catch (Exception e) {
			return null;
		}
		
	}

	/**
	 * Enum representing the types of log entries.
	 */
	private LogType TryParseType(String type) {
		try {
			return type == null? null : LogType.valueOf(type);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Represents the log levels for logging messages.
	 */
	private LogLevel TryParseLevel(String level) {
		try {
			return level== null? null : LogLevel.valueOf(level);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Represents the log level for logging messages.
	 */
	public LogLevel getLevel() {
		return level;
	}

	/**
	 * Sets the log level for the TrickLogFilter.
	 *
	 * @param level the log level to be set
	 */
	public void setLevel(LogLevel level) {
		this.level = level;
	}

	/**
	 * Represents the type of log.
	 */
	public LogType getType() {
		return type;
	}

	/**
	 * Sets the type of the log.
	 *
	 * @param type the type of the log
	 */
	public void setType(LogType type) {
		this.type = type;
	}
	
	/**
	 * Checks if the order is descending.
	 * 
	 * @return true if the order is descending, false otherwise.
	 */
	public boolean isOrderDescending() {
		return getDirection()!=null && getDirection().equalsIgnoreCase("desc");
	}

	/**
	 * This class represents the action performed on a log.
	 */
	public LogAction getAction() {
		return action;
	}

	/**
	 * Sets the action for the TrickLogFilter.
	 *
	 * @param action the action to set
	 */
	public void setAction(LogAction action) {
		this.action = action;
	}

	/**
	 * Returns the author of the trick log.
	 *
	 * @return the author of the trick log
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author of the trick log.
	 *
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns a string representation of the TrickLogFilter object.
	 * 
	 * @return a string representation of the TrickLogFilter object
	 */
	@Override
	public String toString() {
		return "TrickLogFilter [type=" + type + ", level=" + level + ", action=" + action + ", author=" + author + ", size=" + getSize() + ", direction="
				+ getDirection() + "]";
	}
	
}
