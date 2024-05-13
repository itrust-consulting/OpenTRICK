/**
 * 
 */
package lu.itrust.business.ts.usermanagement;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.model.general.Credential;
import lu.itrust.business.ts.model.general.TicketingSystem;


/**
 * Represents a user credential in the system.
 * Extends the base class Credential.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiUser", "fiTicketingSystem" }))
@AttributeOverride(name = "id", column = @Column(name = "idUserCredential"))
public class UserCredential extends Credential {

	@ManyToOne
	@JoinColumn(name = "fiTicketingSystem")
	@Cascade(CascadeType.SAVE_UPDATE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private TicketingSystem ticketingSystem;

	@Column(name = "dtPublicURL")
	private String publicUrl;

	public TicketingSystem getTicketingSystem() {
		return ticketingSystem;
	}

	/**
	 * Sets the ticketing system for the user credential.
	 *
	 * @param ticketingSystem the ticketing system to be set
	 */
	public void setTicketingSystem(TicketingSystem ticketingSystem) {
		this.ticketingSystem = ticketingSystem;
	}

	/**
	 * Returns the public URL.
	 *
	 * @return the public URL as a String.
	 */
	public String getPublicUrl() {
		return publicUrl;
	}

	/**
	 * Sets the public URL for the user credential.
	 *
	 * @param publicUrl the public URL to set
	 */
	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

}
