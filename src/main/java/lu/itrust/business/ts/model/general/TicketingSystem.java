package lu.itrust.business.ts.model.general;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Represents a ticketing system in the application.
 * This class is used to store information about a ticketing system, such as its type, name, URL, enabled status, tracker, customer, and email template.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TicketingSystem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idTicketingSystem")
	private long id;

	@Column(name = "dtType")
	@Enumerated(EnumType.STRING)
	private TicketingSystemType type;

	@Column(name = "dtName")
	private String name;

	@Column(name = "dtURL")
	private String url;

	@Column(name = "dtEnabled")
	private boolean enabled;

	@Column(name = "dtTracker")
	private String tracker;

	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiCustomer")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Customer customer;

	

	@ManyToOne
	@Cascade(CascadeType.ALL)
	@JoinColumn(name = "fiEmailTemplate")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private EmailTemplate emailTemplate;

	/**
	 * Returns the ID of the ticketing system.
	 *
	 * @return the ID of the ticketing system
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the ticketing system.
	 *
	 * @param id the ID to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Represents the type of a ticketing system.
	 */
	public TicketingSystemType getType() {
		return type;
	}

	/**
	 * Sets the type of the ticketing system.
	 *
	 * @param type the type of the ticketing system
	 */
	public void setType(TicketingSystemType type) {
		this.type = type;
	}

	/**
	 * Returns the name of the ticketing system.
	 *
	 * @return the name of the ticketing system
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the ticketing system.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the URL of the ticketing system.
	 *
	 * @return the URL of the ticketing system
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL of the ticketing system.
	 *
	 * @param url the URL to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns whether the ticketing system is enabled or not.
	 *
	 * @return true if the ticketing system is enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled status of the ticketing system.
	 *
	 * @param enabled the new enabled status
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the tracker associated with the ticketing system.
	 *
	 * @return the tracker as a String
	 */
	public String getTracker() {
		return tracker;
	}

	/**
	 * Sets the tracker for the ticketing system.
	 *
	 * @param tracker the tracker to set
	 */
	public void setTracker(String tracker) {
		this.tracker = tracker;
	}


	/**
	 * Returns the customer associated with this ticketing system.
	 *
	 * @return the customer object
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Sets the customer for the ticketing system.
	 *
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Returns the email template used by the ticketing system.
	 *
	 * @return the email template
	 */
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	/**
	 * Sets the email template for the ticketing system.
	 *
	 * @param emailTemplate the email template to be set
	 */
	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
}
