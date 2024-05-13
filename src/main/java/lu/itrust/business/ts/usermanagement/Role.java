package lu.itrust.business.ts.usermanagement;

import java.io.Serializable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Role.java: <br>
 * Detailed description...
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role implements Serializable {

	@Transient
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRole")
	private int id = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtType", nullable = false, unique = true)
	private RoleType type = null;

	/**
	 * Constructor: <br>
	 */
	public Role() {
	}

	/**
	 * @param user
	 * @param role
	 */
	public Role(RoleType role) {
		this.setType(role);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the role
	 */
	public RoleType getType() {
		return type;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setType(RoleType role) {
		this.type = role;
	}

	/**
	 * Returns a hash code value for the object. This method is used by the Java
	 * hashing algorithms when storing objects in hash tables.
	 *
	 * @return the hash code value for the object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * Returns the role name.
	 *
	 * @return the role name as a String.
	 */
	public String getRoleName() {
		return type.name().replace("ROLE_", "");
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * The equality is determined based on the id and type of the Role objects.
	 * 
	 * @param obj the reference object with which to compare
	 * @return true if this object is the same as the obj argument; false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (id != -1 && other.id != -1 && id != other.id)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}