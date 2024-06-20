/**
 * 
 */
package lu.itrust.business.ts.model.general;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * The TrickLog class represents a log entry for a trick in the system.
 * It contains information such as the log level, log type, code, message, author, action, creation timestamp, and parameters.
 * 
 * This class is annotated with JPA annotations to map it to a database table.
 * It also includes various constructors and getter/setter methods for accessing and modifying the log properties.
 * 
 * The class provides a utility method to convert an array to a list.
 * 
 * The class also includes a method to format the log entry in the Log4J format.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TrickLog {


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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

	@Column(name = "dtMessage",length=65536)
	private String message;

	@Column(name = "dtAuthor")
	private String author;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtAction")
	private LogAction action;

	@Column(name = "dtCreated")
	private Timestamp created;

	@Lob
	@ElementCollection
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "TrickLogParameters", joinColumns = @JoinColumn(name = "fiTrickLog"))
	@Column(name = "dtParameter",length=32768)
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

	/**
	 * Converts an array to a list.
	 *
	 * @param array the array to be converted
	 * @param <T>   the type of elements in the array
	 * @return a list containing the elements of the array
	 */
	public static <T> List<T> ToList(T[] array) {
		if (array == null)
			return new LinkedList<T>();
		List<T> tList = new ArrayList<T>(array.length);
		for (T t : array)
			tList.add(t);
		return tList;
	}

	/**
	 * Returns the ID of the TrickLog.
	 *
	 * @return the ID of the TrickLog
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the TrickLog.
	 *
	 * @param id the ID to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Represents the log level of a trick log.
	 */
	public LogLevel getLevel() {
		return level;
	}

	/**
	 * Sets the log level for this TrickLog instance.
	 *
	 * @param level the log level to set
	 */
	public void setLevel(LogLevel level) {
		this.level = level;
	}

	/**
	 * Represents the type of a log.
	 */
	public LogType getType() {
		return type;
	}

	/**
	 * Sets the type of the trick log.
	 *
	 * @param type the type of the trick log
	 */
	public void setType(LogType type) {
		this.type = type;
	}

	/**
	 * Returns the code associated with this TrickLog.
	 *
	 * @return the code associated with this TrickLog
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code for the TrickLog.
	 *
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Returns the message associated with this TrickLog.
	 *
	 * @return the message associated with this TrickLog
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message for the TrickLog.
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the timestamp when the TrickLog was created.
	 *
	 * @return the created timestamp
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * Sets the timestamp when the trick log was created.
	 *
	 * @param created the timestamp when the trick log was created
	 */
	public void setCreated(Timestamp created) {
		this.created = created;
	}

	/**
	 * Returns the list of parameters associated with this TrickLog.
	 *
	 * @return the list of parameters
	 */
	public List<String> getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters for the TrickLog.
	 *
	 * @param parameters the list of parameters to set
	 */
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
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
	 * Represents an action performed in a log entry.
	 */
	public LogAction getAction() {
		return action;
	}

	/**
	 * Sets the action for the TrickLog.
	 *
	 * @param action the action to set
	 */
	public void setAction(LogAction action) {
		this.action = action;
	}

	/**
	 * Converts the TrickLog object to a formatted string in the Log4J format.
	 *
	 * @return The formatted string representation of the TrickLog object.
	 */
	public String toLog4J() {
		return String.format("%s %s %s %s %s %s", created, level, type, action, author, message);
	}

}
