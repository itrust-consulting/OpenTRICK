/**
 * 
 */
package lu.itrust.business.TS.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author eomar
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomerForm {

	private int id;

	private String organisation;

	private String address;

	private String city;

	private String zipCode;

	private String country;

	private String contactPerson;

	private String phoneNumber;

	private String email;

	private boolean canBeUsed = true;
	
	private TicketingSystemForm ticketingSystem;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isCanBeUsed() {
		return canBeUsed;
	}

	public void setCanBeUsed(boolean canBeUsed) {
		this.canBeUsed = canBeUsed;
	}

	public TicketingSystemForm getTicketingSystem() {
		return ticketingSystem;
	}

	public void setTicketingSystem(TicketingSystemForm ticketingSystem) {
		this.ticketingSystem = ticketingSystem;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}
