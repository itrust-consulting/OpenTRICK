/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"dtTaskId", "fiProject"}))
public class TicketLink {

	@Id
	@Column(name="idTicket")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="dtTaskId")
	private String taskId;
	
	@Column(name="dtMeasureRef")
	private String measureRef;
	
	@Column(name="dtStandard")
	private String standard;
	
	/**
	 * 
	 */
	public TicketLink() {

	}

	/**
	 * @param taskId
	 * @param measureRef
	 * @param standard
	 */
	public TicketLink(String taskId, String measureRef, String standard) {
		this.taskId = taskId;
		this.measureRef = measureRef;
		this.standard = standard;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the measureRef
	 */
	public String getMeasureRef() {
		return measureRef;
	}

	/**
	 * @param measureRef the measureRef to set
	 */
	public void setMeasureRef(String measureRef) {
		this.measureRef = measureRef;
	}

	/**
	 * @return the standard
	 */
	public String getStandard() {
		return standard;
	}


	/**
	 * @param standard the standard to set
	 */
	public void setStandard(String standard) {
		this.standard = standard;
	}
}
