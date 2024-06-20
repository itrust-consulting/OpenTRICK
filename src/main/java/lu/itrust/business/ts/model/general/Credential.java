package lu.itrust.business.ts.model.general;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * This class represents a credential used for authentication or authorization purposes.
 * It is an abstract class that provides common properties and methods for all types of credentials.
 *
 * @see Cacheable
 * @see Cache
 * @see MappedSuperclass
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MappedSuperclass
public abstract class Credential {
	
	@Transient
	public final static String VALUE_PROPERTY_NAME="value";

	@Id
	@Column(name = "idCredential")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "dtType")
	@Enumerated(EnumType.STRING)
	private CredentialType type;

	@Column(name = "dtName")
	private String name;

	@Column(name = "dtValue", length = 2047)
	private String value;

	/**
	 * Returns the ID of the Credential.
	 *
	 * @return the ID of the Credential
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the Credential.
	 *
	 * @param id the ID of the Credential
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the type of the Credential.
	 *
	 * @return the type of the Credential
	 */
	public CredentialType getType() {
		return type;
	}

	/**
	 * Sets the type of the Credential.
	 *
	 * @param type the type of the Credential
	 */
	public void setType(CredentialType type) {
		this.type = type;
	}

	/**
	 * Returns the name of the Credential.
	 *
	 * @return the name of the Credential
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Credential.
	 *
	 * @param name the name of the Credential
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value of the Credential.
	 *
	 * @return the value of the Credential
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the Credential.
	 *
	 * @param value the value of the Credential
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
