/**
 * 
 */
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
 * @author eomar
 *
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CredentialType getType() {
		return type;
	}

	public void setType(CredentialType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
