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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TicketingSystemType getType() {
		return type;
	}

	public void setType(TicketingSystemType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTracker() {
		return tracker;
	}

	public void setTracker(String tracker) {
		this.tracker = tracker;
	}


	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
}
