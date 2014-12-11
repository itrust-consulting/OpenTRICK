package lu.itrust.business.TS.data.general;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;

/**
 * Customer: <br>
 * This class represents an Customer and all its data.
 * 
 * This class is used to store Customers. Each Analysis is done for a customer.
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
public class Customer {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The customer ID */
	@Id
	@GeneratedValue
	@Column(name = "idCustomer")
	private int id = -1;

	/** The Name of the organisation */
	@Column(name = "dtOrganisation", unique = true, nullable = false)
	private String organisation = "";

	/** The Address of the Organisation */
	@Column(name = "dtAddress", nullable = false)
	private String address = "";

	/** The City where the Organisation is located */
	@Column(name = "dtCity", nullable = false)
	private String city = "";

	/** The ZIP Code of the Organisation location */
	@Column(name = "dtZIP", nullable = false, length = 20)
	private String ZIPCode = "";

	/** The Country where the Organisation is located */
	@Column(name = "dtCountry", nullable = false)
	private String country = "";

	/** The Name of the Contact Person in the Organisation */
	@Column(name = "dtContactPerson", nullable = false)
	private String contactPerson = "";

	/** The Telephone Number of the Contact Person or Organisation */
	@Column(name = "dtTelephone", nullable = false)
	private String phoneNumber = "";

	/** The Email of the Contact Person or Organisation */
	@Column(name = "dtEmail", nullable = false)
	private String email = "";

	@Column(name = "dtCanBeUsed", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean canBeUsed = true;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getId: <br>
	 * Returns the "id" field value
	 * 
	 * @return The Customer ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *            The value to set the Customer ID
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.customer.id", "Customer id should be greater than 0");
		this.id = id;
	}

	/**
	 * getOrganisation: <br>
	 * Returns the "organisation" field value
	 * 
	 * @return The Organisation Name
	 */
	public String getOrganisation() {
		return organisation;
	}

	/**
	 * setOrganisation: <br>
	 * Sets the "organisation" field with a value
	 * 
	 * @param organsiation
	 *            The value to set the Name of the Organisation
	 * @throws TrickException
	 */
	public void setOrganisation(String organisation) throws TrickException {
		if (organisation == null || organisation.trim().isEmpty())
			throw new TrickException("error.customer.organisation.empty", "Organisation cannot be empty!");
		this.organisation = organisation;
	}

	/**
	 * getAddress: <br>
	 * Returns the "address" field value
	 * 
	 * @return The Address (from the Organisation Location)
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * setAddress: <br>
	 * Sets the "address" field with a value
	 * 
	 * @param address
	 *            The value to set the Address (from the Organisation Location)
	 * @throws TrickException
	 */
	public void setAddress(String address) throws TrickException {
		if (address == null || address.trim().isEmpty())
			throw new TrickException("error.customer.address.empty", "Address cannot be empty!");
		this.address = address;
	}

	/**
	 * getCity: <br>
	 * Returns the "city" field value
	 * 
	 * @return The City Name (from the Organisation Location)
	 */
	public String getCity() {
		return city;
	}

	/**
	 * setCity: <br>
	 * Sets the "city" field with a value
	 * 
	 * @param city
	 *            The value to set the City Name (from the Organisation Location)
	 * @throws TrickException
	 */
	public void setCity(String city) throws TrickException {
		if (city == null || !city.matches(Constant.REGEXP_VALID_NAME))
			throw new TrickException("error.customer.address.rejected", "City has been rejected!");
		this.city = city;
	}

	/**
	 * getZIPCode: <br>
	 * Returns the "ZIPCode" field value
	 * 
	 * @return The ZIP Code (from the Organisation Location)
	 */
	public String getZIPCode() {
		return ZIPCode;
	}

	/**
	 * setZIPCode Sets the "ZIPCode" field with a value
	 * 
	 * @param ZIPCode
	 *            The value to set the ZIP Code (from the Organisation Location)
	 * @throws TrickException
	 */
	public void setZIPCode(String ZIPCode) throws TrickException {
		if (ZIPCode == null || ZIPCode.trim().isEmpty())
			throw new TrickException("error.customer.zipcode.empty", "ZIPCode cannot be empty!");
		this.ZIPCode = ZIPCode;
	}

	/**
	 * getCountry: <br>
	 * Returns the "country" field value
	 * 
	 * @return The name of the Country (from the Organisation Location)
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * setCountry: <br>
	 * Sets the "country" field with a value
	 * 
	 * @param country
	 *            The value to set the Country Name (from the Organisation Location)
	 * @throws TrickException
	 */
	public void setCountry(String country) throws TrickException {
		if (country == null || !country.matches(Constant.REGEXP_VALID_NAME))
			throw new TrickException("error.customer.country.empty", "Country cannot be empty!");
		this.country = country;
	}

	/**
	 * getContactPerson: <br>
	 * Returns the "contactPerson" field value
	 * 
	 * @return The Contact Person Name
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * setContactPerson: <br>
	 * Sets the "contactPerson" field with a value
	 * 
	 * @param contactPerson
	 *            The value to set the Contact Person Name
	 * @throws TrickException
	 */
	public void setContactPerson(String contactPerson) throws TrickException {
		if (contactPerson == null || !contactPerson.matches(Constant.REGEXP_VALID_NAME))
			throw new TrickException("error.customer.contact_person.empty", "Contact person cannot be empty!");
		this.contactPerson = contactPerson;
	}

	/**
	 * getTelephoneNumber: <br>
	 * Returns the "telephoneNumber" field value
	 * 
	 * @return The Telephone Number (from the Contact Person or Organisation)
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * setTelephoneNumber: <br>
	 * Sets the "telephoneNumber" field with a value
	 * 
	 * @param telephoneNumber
	 *            The value to set the Telephone Number (from the Contact Person or Organisation)
	 * @throws TrickException
	 */
	public void setPhoneNumber(String telephoneNumber) throws TrickException {
		if (telephoneNumber == null || telephoneNumber.trim().isEmpty())
			throw new TrickException("error.customer.telephone_number.empty", "Telephone number cannot be empty");
		this.phoneNumber = telephoneNumber;
	}

	/**
	 * getEmail: <br>
	 * Returns the "email" field value
	 * 
	 * @return The Email Address (from the Contact Person or Organisation)
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * setEmail: <br>
	 * Sets the "email" field with a value
	 * 
	 * @param email
	 *            The value to set the Email (from the Contact Person or Organisation)
	 * @throws TrickException
	 */
	public void setEmail(String email) throws TrickException {
		if (email == null || !email.matches(Constant.REGEXP_VALID_EMAIL))
			throw new TrickException("error.customer.email.empty", "Email address was rejected");
		this.email = email;
	}

	/**
	 * @return the canBeUsed
	 */
	public boolean isCanBeUsed() {
		return canBeUsed;
	}

	/**
	 * @param canBeUsed
	 *            the canBeUsed to set
	 */
	public void setCanBeUsed(boolean canBeUsed) {
		this.canBeUsed = canBeUsed;
	}

	/**
	 * hashCode:<br>
	 * Used inside equals method.<br>
	 * <br>
	 * <b>NOTE:</b> This Method is auto generated!
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/**
	 * equals:<br>
	 * Checks if object of this class equals another. This means: the field id distincts two
	 * different objects. <br>
	 * <br>
	 * <b>NOTE:</b> This Method is auto generated!
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Customer other = (Customer) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	/**
	 * toString: <br>
	 * Description
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Customer [id=" + id + ", organisation=" + organisation + ", address=" + address + ", city=" + city + ", ZIPCode=" + ZIPCode + ", country=" + country + ", contactPerson="
			+ contactPerson + ", telephoneNumber=" + phoneNumber + ", email=" + email + "]";
	}
}