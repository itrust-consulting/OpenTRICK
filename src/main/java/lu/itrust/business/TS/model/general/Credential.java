/**
 * 
 */
package lu.itrust.business.TS.model.general;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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
	
	@Id
	@Column(name="idCredential")
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private long id;
	
	@Column(name="dtType")
	@Enumerated(EnumType.STRING)
	private CredentialType type;
	
	@Column(name="dtName")
	private String name;
	
	@Column(name="dtValue")
	private String value;
	
	@Column(name="dtIV")
	private String iv;

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

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}
}
