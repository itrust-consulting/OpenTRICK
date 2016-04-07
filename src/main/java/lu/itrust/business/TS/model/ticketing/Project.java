/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;



/**
 * @author eomar
 *
 */
@Entity
public class Project {

	@Id
	@Column(name="idProject")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="dtIdentifier")
	private String identifier;
	
	@Column(name="dtProjectId")
	private String projectId;
	
	@OneToMany
	@JoinColumn(name="fiProject")
	List<Ticket> tickets;
	
	/**
	 * 
	 */
	public Project() {
	}
	
	/**
	 * @param analysisId
	 * @param projectId
	 */
	public Project(String identifier, String projectId) {
		this.identifier = identifier;
		this.projectId = projectId;
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
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
