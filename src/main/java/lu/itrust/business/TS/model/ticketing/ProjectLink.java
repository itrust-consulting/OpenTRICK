/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author eomar
 *
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "dtProjectId", "dtType" }))
public class ProjectLink {

	@Id
	@Column(name = "idProjectLink")
	private String id;

	@Column(name = "dtProjectId", unique = true)
	private String projectId;

	@Column(name = "dtType")
	@Enumerated(EnumType.STRING)
	private ProjectType type;

	@OneToMany
	@JoinColumn(name = "fiProjectLink")
	@Cascade(CascadeType.ALL)
	private List<TicketLink> ticketLinks = new LinkedList<TicketLink>();

	/**
	 * 
	 */
	public ProjectLink() {
	}

	/**
	 * @param projectId
	 * @param type
	 * @param ticketLinks
	 */
	public ProjectLink(String projectId, ProjectType type) {
		this.projectId = projectId;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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

	/**
	 * @return the type
	 */
	public ProjectType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ProjectType type) {
		this.type = type;
	}

	/**
	 * @return the ticketLinks
	 */
	public List<TicketLink> getTicketLinks() {
		return ticketLinks;
	}

	/**
	 * @param ticketLinks the ticketLinks to set
	 */
	public void setTicketLinks(List<TicketLink> ticketLinks) {
		this.ticketLinks = ticketLinks;
	}

}
