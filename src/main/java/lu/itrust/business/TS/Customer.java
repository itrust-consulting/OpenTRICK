package lu.itrust.business.TS;

import java.io.Serializable;

import org.hibernate.validator.constraints.impl.EmailValidator;

import lu.itrust.business.TS.tsconstant.Constant;

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
public class Customer implements Serializable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The customer ID */
	private int id = -1;

	/** The Name of the organisation */
	private String organisation = "";

	/** The Address of the Organisation */
	private String address = "";

	/** The City where the Organisation is located */
	private String city = "";

	/** The ZIP Code of the Organisation location */
	private String ZIPCode = "";

	/** The Country where the Organisation is located */
	private String country = "";

	/** The Name of the Contact Person in the Organisation */
	private String contactPerson = "";

	/** The Telephone Number of the Contact Person or Organisation */
	private String telephoneNumber = "";

	/** The Email of the Contact Person or Organisation */
	private String email = "";

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
	 */
	public void setId(int id) {
		if (id < 1) {
			throw new IllegalArgumentException("Customer ID field cannot be < 1!");
		}
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
	 */
	public void setOrganisation(String organisation) {
		if ((organisation == null) || (organisation.trim().equals(""))) {
			throw new IllegalArgumentException("Customer Organisation field cannot be null or empty!");
		}
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
	 */
	public void setAddress(String address) {
		if ((address == null) || (address.trim().equals(""))) {
			throw new IllegalArgumentException("Customer Address field cannot be null or empty!");
		}
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
	 */
	public void setCity(String city) {
		if ((city == null) || (city.trim().equals("")) || (!city.matches(Constant.REGEXP_VALID_NAME))) {
			throw new IllegalArgumentException("Customer City field cannot be null or empty!");
		}
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
	 */
	public void setZIPCode(String ZIPCode) {
		if ((ZIPCode == null) || (ZIPCode.trim().equals(""))) {
			throw new IllegalArgumentException("Customer ZIPCode field cannot be null or empty!");
		}
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
	 */
	public void setCountry(String country) {
		if ((country == null) || (country.trim().equals("")) || (!country.matches(Constant.REGEXP_VALID_NAME))) {
			throw new IllegalArgumentException("Customer Country field cannot be null or empty!");
		}
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
	 */
	public void setContactPerson(String contactPerson) {
		if ((contactPerson == null) || (contactPerson.trim().equals("")) || (!contactPerson.matches(Constant.REGEXP_VALID_NAME))) {
			throw new IllegalArgumentException("Customer Contact Person field cannot be null or empty!");
		}
		this.contactPerson = contactPerson;
	}

	/**
	 * getTelephoneNumber: <br>
	 * Returns the "telephoneNumber" field value
	 * 
	 * @return The Telephone Number (from the Contact Person or Organisation)
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * setTelephoneNumber: <br>
	 * Sets the "telephoneNumber" field with a value
	 * 
	 * @param telephoneNumber
	 *            The value to set the Telephone Number (from the Contact Person or Organisation)
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		if ((telephoneNumber == null) || (telephoneNumber.trim().equals("")) || (!telephoneNumber.matches(Constant.REGEXP_VALID_PHONE))) {
			throw new IllegalArgumentException("Customer Telephone Number field cannot be null or empty and has to match Regualr Expression: " + Constant.REGEXP_VALID_PHONE);
		}
		this.telephoneNumber = telephoneNumber;
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
	 */
	public void setEmail(String email) {
		if (((email == null) || (email.trim().equals("")) || (!email.matches(Constant.REGEXP_VALID_EMAIL)))) {
			throw new IllegalArgumentException("Customer Email field cannot be null or empty and needs to be valid!");
		}
		this.email = email;
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
		return "Customer [id=" + id + ", organisation=" + organisation + ", address=" + address + ", city=" + city + ", ZIPCode=" + ZIPCode + ", country=" + country
			+ ", contactPerson=" + contactPerson + ", telephoneNumber=" + telephoneNumber + ", email=" + email + "]";
	}
}