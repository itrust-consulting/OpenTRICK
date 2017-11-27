package lu.itrust.business.TS.model.general.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Notification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String type;
	
	private String code;
	
	private boolean once;

	private Object[] parameters;

	private Map<String, String> messages = new HashMap<>(2);

	/**
	 * 
	 */
	public Notification() {
	}

	/**
	 * @param id
	 * @param code
	 * @param type
	 * @param parameters
	 * @param messages
	 */
	public Notification(String code, String type) {
		this.id = UUID.randomUUID().toString();
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

	public String getType() {
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

	public void setType(String type) {
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

}
