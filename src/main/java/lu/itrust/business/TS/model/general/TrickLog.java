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
	@Column(name="idTrickLog")
	private long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="dtLevel")
	private LogLevel level;
	
	@Column(name="dtCode")
	private String code;
	
	@Column(name="dtMessage")
	private String message;
	
	@Column(name="dtCreated")
	private Timestamp created;
	
	@ElementCollection
	@JoinTable(name = "TrickLogParameters", joinColumns = @JoinColumn(name = "fiTrickLog"))
	@Column(name = "parameter")
	@Cascade(CascadeType.ALL)
	private List<String> parameters;
	
	
	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public TrickLog(LogLevel level, String code, String message, String... parameters) {
		this.level = level;
		this.code = code;
		this.message = message;
		this.parameters = ToList(parameters);
		this.created = new Timestamp(System.currentTimeMillis());
	}
	

	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public TrickLog(LogLevel level, String code, String message, List<String> parameters) {
		this.level = level;
		this.code = code;
		this.message = message;
		this.parameters = parameters;
		this.created = new Timestamp(System.currentTimeMillis());
	}
	
	
	public static <T> List<T> ToList(T[] array){
		if(array == null)
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

	

}
