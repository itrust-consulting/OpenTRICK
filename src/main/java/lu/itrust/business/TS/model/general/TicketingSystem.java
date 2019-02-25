package lu.itrust.business.TS.model.general;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TicketingSystem {

	@Id
	@GeneratedValue
	@Column(name="idTicketingSystem")
	private long id;

	@Column(name="dtType")
	private TicketingSystemType type;

	@Column(name="dtName")
	private String name;

	@Column(name="dtURL")
	private String url;
	
	@Column(name="dtEnabled")
	private boolean enabled;

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

}
