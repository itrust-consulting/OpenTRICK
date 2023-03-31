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
 * @author eomar
 *
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

	public TicketingSystem getTicketingSystem() {
		return ticketingSystem;
	}

	public void setTicketingSystem(TicketingSystem ticketingSystem) {
		this.ticketingSystem = ticketingSystem;
	}

}
