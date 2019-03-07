/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.general.Credential;
import lu.itrust.business.TS.model.general.TicketingSystem;

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
