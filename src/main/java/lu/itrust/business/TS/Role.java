/**
 * 
 */
package lu.itrust.business.TS;

import java.io.Serializable;

/**
 * @author oensuifudine
 * 
 */
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private long id = - 1;

	private RoleType type = null;

	/**
	 * 
	 */
	public Role() {
		// TODO Auto-generated constructor stub
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
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
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
