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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "dtReferenceId", "dtType" }))
public class Project {

	@Id
	@Column(name = "idProject")
	private String id;

	@Column(name = "dtReferenceId", unique = true)
	private String referenceId;

	@Column(name = "dtType")
	@Enumerated(EnumType.STRING)
	private ProjectType type;

	@OneToMany
	@JoinColumn(name = "fiProject")
	@Cascade(CascadeType.ALL)
	private List<Ticket> tickets = new LinkedList<Ticket>();

	/**
	 * 
	 */
	public Project() {
	}

	/**
	 * @param analysisId
	 * @param referenceId
	 */
	public Project(String id, String referenceId, ProjectType type) {
		this.id = id;
		this.referenceId = referenceId;
		this.type = type;
	}

	/**
	 * @return the referenceId
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * @param referenceId
	 *            the referenceId to set
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
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
	 * @return the tickets
	 */
	public List<Ticket> getTickets() {
		return tickets;
	}

	/**
	 * @param tickets
	 *            the tickets to set
	 */
	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
}
