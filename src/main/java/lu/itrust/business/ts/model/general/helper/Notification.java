package lu.itrust.business.ts.model.general.helper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.StringUtils;

import lu.itrust.business.ts.model.general.LogLevel;

/**
 * The `Notification` class represents a notification with a unique identifier, type, code, messages, and other properties.
 * It provides methods to get and set the code, messages, type, and other attributes of the notification.
 * The class also includes methods to check the validity of the notification, update its properties, and determine if it should be shown at a given date.
 */
public class Notification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private LogLevel type;

	private String code;

	private boolean once;

	private Object[] parameters;

	private Map<String, String> messages = new HashMap<>(2);

	private Timestamp created;

	private Timestamp startDate;

	private Timestamp endDate;

	/**
	 * 
	 */
	public Notification() {
		this.id = UUID.randomUUID().toString();
		this.created = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @param id
	 * @param code
	 * @param type
	 * @param parameters
	 * @param messages
	 */
	public Notification(String code, LogLevel type) {
		this();
		this.code = code;
		this.type = type;
	}

	/**
	 * Returns the code associated with this notification.
	 *
	 * @return the code associated with this notification
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Returns the messages associated with this notification.
	 *
	 * @return a map containing the messages as key-value pairs
	 */
	public Map<String, String> getMessages() {
		return messages;
	}

	/**
	 * Returns the parameters associated with this notification.
	 *
	 * @return an array of objects representing the parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * Represents the log level of a notification.
	 */
	public LogLevel getType() {
		return type;
	}

	/**
	 * Sets the code for the notification.
	 *
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Sets the messages for the notification.
	 *
	 * @param messages a map containing the messages to be set
	 */
	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	/**
	 * Sets the parameters for the notification.
	 *
	 * @param parameters an array of objects representing the parameters
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * Sets the type of the notification.
	 *
	 * @param type the log level to set
	 */
	public void setType(LogLevel type) {
		this.type = type;
	}

	/**
	 * Returns the ID of the notification.
	 *
	 * @return the ID of the notification as a String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the ID of the notification.
	 *
	 * @param id the ID to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Represents a notification that can be added to a collection of messages.
	 */
	public Notification add(Locale locale, String message) {
		messages.put(locale.getLanguage(), message);
		return this;
	}

	/**
	 * Removes the notification for the specified locale.
	 * 
	 * @param locale the locale for which to remove the notification
	 * @return the updated Notification object
	 */
	public Notification remove(Locale locale) {
		messages.remove(locale.getLanguage());
		return this;
	}

	/**
	 * Returns whether the notification should only occur once.
	 *
	 * @return true if the notification should only occur once, false otherwise
	 */
	public boolean isOnce() {
		return once;
	}

	/**
	 * Sets the flag indicating whether the notification should be sent only once.
	 *
	 * @param once true if the notification should be sent only once, false otherwise
	 */
	public void setOnce(boolean once) {
		this.once = once;
	}

	/**
	 * Represents a point in time, measured in milliseconds since the epoch (January 1, 1970 00:00:00.000 GMT).
	 * The Timestamp class is a thin wrapper around java.util.Date that allows the JDBC API to identify this as an SQL TIMESTAMP value.
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * Sets the timestamp when the notification was created.
	 *
	 * @param created the timestamp when the notification was created
	 */
	public void setCreated(Timestamp created) {
		this.created = created;
	}

	/**
	 * Checks if the notification is valid.
	 * 
	 * @return true if the notification is valid, false otherwise
	 */
	public boolean isValid() {
		return isValidId() && (StringUtils.hasText(this.code) || isEmpty());
	}

	/**
	 * Checks if the ID of the notification is valid.
	 * 
	 * @return true if the ID is valid, false otherwise
	 */
	private boolean isValidId() {
		try {
			return StringUtils.hasText(this.id) && UUID.fromString(id).toString().equals(id);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks if the notification is empty.
	 * 
	 * @return true if the notification is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.messages == null || !this.messages.values().stream().anyMatch(value -> !(value == null || value.trim().isEmpty()));
	}

	/**
	 * Updates the notification.
	 * 
	 * If the ID is not valid, generates a new random ID.
	 * If the creation timestamp is null, sets it to the current timestamp.
	 * 
	 * @return true if the notification code has text or is not empty, false otherwise.
	 */
	public boolean update() {
		if (!isValidId())
			this.id = UUID.randomUUID().toString();
		if (this.created == null)
			this.created = new Timestamp(System.currentTimeMillis());
		return StringUtils.hasText(this.code) || !isEmpty();
	}

	/**
	 * Updates a notification.
	 */
	public Notification update(Notification notification) {
		this.code = notification.code;
		this.messages = notification.messages;
		this.parameters = notification.parameters;
		this.once = notification.once;
		this.endDate = notification.endDate;
		this.startDate = notification.startDate;
		return this;

	}

	/**
	 * Returns the start date of the notification.
	 *
	 * @return the start date of the notification
	 */
	public Timestamp getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date of the notification.
	 *
	 * @param startDate the start date to set
	 */
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	/**
	 * Returns the end date of the notification.
	 *
	 * @return the end date of the notification
	 */
	public Timestamp getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date of the notification.
	 *
	 * @param endDate the end date to be set
	 */
	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	/**
	 * Checks if the notification should be shown at a given date.
	 *
	 * @param date The date to check against.
	 * @return true if the notification should be shown, false otherwise.
	 */
	public boolean isShowning(long date) {
		if (isOnce())
			return true;
		if (this.startDate == null)
			return endDate == null || endDate.getTime() >= date;
		return this.startDate.getTime() <= date && (endDate == null || endDate.getTime() >= date);
	}

}
