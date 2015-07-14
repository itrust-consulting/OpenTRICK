/**
 * 
 */
package lu.itrust.business.TS.model.general;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author eomar
 *
 */
@Entity
public class TrickLog {

	@Id
	@GeneratedValue
	@Column(name = "idTrickLog")
	private long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtLevel")
	private LogLevel level;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtType")
	private LogType type;

	@Column(name = "dtCode")
	private String code;

	@Column(name = "dtMessage")
	private String message;

	@Column(name = "dtAuthor")
	private String author;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtAction")
	private LogAction action;

	@Column(name = "dtCreated")
	private Timestamp created;

	@ElementCollection
	@JoinTable(name = "TrickLogParameters", joinColumns = @JoinColumn(name = "fiTrickLog"))
	@Column(name = "parameter")
	@Cascade(CascadeType.ALL)
	private List<String> parameters;

	/**
	 * 
	 */
	public TrickLog() {
	}

	/**
	 * @param code
	 * @param message
	 * @param author
	 * @param action
	 * @param parameters
	 */
	public TrickLog(String code, String message, String author, LogAction action, List<String> parameters) {
		this.type = LogType.GENERAL;
		this.level = LogLevel.INFO;
		this.code = code;
		this.message = message;
		this.parameters = parameters;
		this.author = author;
		this.action = action;
		this.created = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @param code
	 * @param message
	 * @param author
	 * @param action
	 * @param parameters
	 */
	public TrickLog(String code, String message, String author, LogAction action, String... parameters) {
		this.type = LogType.GENERAL;
		this.level = LogLevel.INFO;
		this.code = code;
		this.message = message;
		this.author = author;
		this.action = action;
		this.parameters = ToList(parameters);
		this.created = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param author
	 * @param parameters
	 */
	public TrickLog(LogLevel level, String code, String message, String author, LogAction action, String... parameters) {
		this.type = LogType.GENERAL;
		this.level = level;
		this.code = code;
		this.message = message;
		this.author = author;
		this.action = action;
		this.parameters = ToList(parameters);
		this.created = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public TrickLog(LogLevel level, String code, String message, String author, LogAction action, List<String> parameters) {
		this.type = LogType.GENERAL;
		this.level = level;
		this.code = code;
		this.message = message;
		this.author = author;
		this.action = action;
		this.parameters = parameters;
		this.created = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public TrickLog(LogType type, String code, String message, String author, LogAction action, List<String> parameters) {
		this.level = LogLevel.INFO;
		this.type = type;
		this.code = code;
		this.message = message;
		this.author = author;
		this.action = action;
		this.created = new Timestamp(System.currentTimeMillis());
		this.parameters = parameters;
	}

	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public TrickLog(LogType type, String code, String message, String author, LogAction action, String... parameters) {
		this.level = LogLevel.INFO;
		this.type = type;
		this.code = code;
		this.message = message;
		this.author = author;
		this.action = action;
		this.created = new Timestamp(System.currentTimeMillis());
		this.parameters = ToList(parameters);
	}

	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public TrickLog(LogLevel level, LogType type, String code, String message, String author, LogAction action, List<String> parameters) {
		this.level = level;
		this.type = type;
		this.code = code;
		this.message = message;
		this.author = author;
		this.action = action;
		this.created = new Timestamp(System.currentTimeMillis());
		this.parameters = parameters;
	}

	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public TrickLog(LogLevel level, LogType type, String code, String message, String author, LogAction action, String... parameters) {
		this.level = level;
		this.type = type;
		this.code = code;
		this.message = message;
		this.author = author;
		this.action = action;
		this.created = new Timestamp(System.currentTimeMillis());
		this.parameters = ToList(parameters);
	}

	public static <T> List<T> ToList(T[] array) {
		if (array == null)
			return new LinkedList<T>();
		List<T> tList = new ArrayList<T>(array.length);
		for (T t : array)
			tList.add(t);
		return tList;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LogAction getAction() {
		return action;
	}

	public void setAction(LogAction action) {
		this.action = action;
	}

	public String toLog4J() {
		return String.format("%s %s %s %s %s %s", created, level, type, action, author, message);
	}

}
