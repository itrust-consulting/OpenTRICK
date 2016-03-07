package lu.itrust.business.TS.usermanagement;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Role.java: <br>
 * Detailed description...
 *
 * @author eomar itrust consulting s.a.rl.:
 * @version 
 * @since Aug 19, 2012
 */
@Entity public class Role implements Serializable {

	@Transient
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idRole")
	private int id = - 1;
 
	@Enumerated(EnumType.STRING)
	@Column(name="dtType", nullable=false, unique=true)
	private RoleType type = null;

	/**
	 * Constructor: <br>
	 */
	public Role() {	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	public String getRoleName() {
		return type.name().replace("ROLE_", "");
	}

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