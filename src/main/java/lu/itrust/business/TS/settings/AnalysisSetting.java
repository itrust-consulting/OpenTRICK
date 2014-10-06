package lu.itrust.business.TS.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.usermanagement.User;

/** AnalysisSetting.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version 
 * @since Sep 23, 2014
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames = {"fiAnalysis", "fiUser","dtKey"}))
public class AnalysisSetting implements Serializable,Cloneable {

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	@Column(name="idAnalysisSetting")
	private Integer id = -1;
	
	@ManyToOne
	@JoinColumn(name="fiUser", nullable=false)
	private User user = null;
		
	@Column(name="dtKey", nullable=false)
	private String key = "";
	
	@Column(name="dtValue", nullable=false)
	private String value ="";
	
	public AnalysisSetting (){};
	
	public AnalysisSetting(String key, String value, User user) {
		this.key = key;
		this.value = value;
		this.user = user;
	}
	
	/** getValue: <br>
	 * Returns the value field value.
	 * 
	 * @return The value of the value field
	 */
	public String getValue() {
		return value;
	}
	
	/** setValue: <br>
	 * Sets the Field "value" with a value.
	 * 
	 * @param value 
	 * 			The Value to set the value field
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/** getKey: <br>
	 * Returns the key field value.
	 * 
	 * @return The value of the key field
	 */
	public String getKey() {
		return key;
	}
	
	/** setKey: <br>
	 * Sets the Field "key" with a value.
	 * 
	 * @param key 
	 * 			The Value to set the key field
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/** getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public Integer getId() {
		return id;
	}
	
	/** setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id 
	 * 			The Value to set the id field
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/** getUser: <br>
	 * Returns the user field value.
	 * 
	 * @return The value of the user field
	 */
	public User getUser() {
		return user;
	}

	/** setUser: <br>
	 * Sets the Field "user" with a value.
	 * 
	 * @param user 
	 * 			The Value to set the user field
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AnalysisSetting clone() throws CloneNotSupportedException {
		return (AnalysisSetting) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public AnalysisSetting duplicate() throws CloneNotSupportedException{
		AnalysisSetting copy = (AnalysisSetting) super.clone();
		copy.setId(-1);
		return copy;
	}
	
}