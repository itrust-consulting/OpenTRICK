package lu.itrust.business.TS.model.general.helper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.StringUtils;

import lu.itrust.business.TS.model.general.LogLevel;

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

	public String getCode() {
		return code;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public LogLevel getType() {
		return type;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public void setType(LogLevel type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Notification add(Locale locale, String message) {
		messages.put(locale.getLanguage(), message);
		return this;
	}

	public Notification remove(Locale locale) {
		messages.remove(locale.getLanguage());
		return this;
	}

	public boolean isOnce() {
		return once;
	}

	public void setOnce(boolean once) {
		this.once = once;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public boolean isValid() {
		return isValidId() && (!StringUtils.isEmpty(this.code) || isEmpty());
	}

	private boolean isValidId() {
		try {
			return !StringUtils.isEmpty(this.id) && UUID.fromString(id).toString().equals(id);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isEmpty() {
		return this.messages == null || this.messages.isEmpty();
	}

	public boolean update() {
		if (!isValidId())
			this.id = UUID.randomUUID().toString();
		if (this.created == null)
			this.created = new Timestamp(System.currentTimeMillis());
		return !(StringUtils.isEmpty(this.code) && isEmpty());
	}

	public Notification update(Notification notification) {
		this.code = notification.code;
		this.messages = notification.messages;
		this.parameters = notification.parameters;
		this.once = notification.once;
		this.endDate = notification.endDate;
		this.startDate = notification.startDate;
		return this;

	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public boolean isShowning(long date) {
		if(isOnce())
			return true;
		if (this.startDate == null)
			return endDate == null || endDate.getTime() >= date;
		return this.startDate.getTime() <= date && (endDate == null || endDate.getTime() >= date);
	}

}
