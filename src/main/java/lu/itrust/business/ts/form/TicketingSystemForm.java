/**
 * 
 */
package lu.itrust.business.ts.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lu.itrust.business.ts.model.general.TicketingSystemType;

/**
 * @author eomar
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TicketingSystemForm {
	
	private long id;
	
	private String url;
	
	private String name;

	private String tracker;
	
	private TicketingSystemType type;
	
	private boolean enabled;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TicketingSystemType getType() {
		return type;
	}

	public void setType(TicketingSystemType type) {
		this.type = type;
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
}
