
package lu.itrust.business.TS.usermanagement;

import java.util.LinkedList;
import java.util.List;

public enum RoleType {

	ROLE_IDS, ROLE_USER, ROLE_CONSULTANT, ROLE_ADMIN, ROLE_SUPERVISOR;

	public final static RoleType[] ROLES = { ROLE_USER, ROLE_CONSULTANT, ROLE_ADMIN, ROLE_SUPERVISOR };

	/**
	 * Retrieve all inherited roles<br>
	 * ROLE_IDS -> ROLE_IDS<br>
	 * ROLE_USER -> ROLE_USER<br>
	 * ROLE_CONSULTANT -> ROLE_USER, ROLE_CONSULTANT<br>
	 * @param right
	 * @return inherited roles
	 */
	public static List<RoleType> InheritedRoles(RoleType right) {
		List<RoleType> rights = new LinkedList<RoleType>();
		if (right != null) {
			if (right == ROLE_IDS)
				rights.add(ROLE_IDS);
			else {
				for (int i = right.ordinal() - 1; i >= 0; i--)
					rights.add(ROLES[i]);
			}
		}
		return rights;
	}
	
	/**
	 * Retrieve all greater roles<br>
	 * ROLE_IDS -> ROLE_IDS<br>
	 * ROLE_USER -> ROLE_USER, ROLE_CONSULTANT, ROLE_ADMIN, ROLE_SUPERVISOR <br>
	 * ROLE_CONSULTANT -> ROLE_CONSULTANT, ROLE_ADMIN, ROLE_SUPERVISOR<br>
	 * @param right
	 * @return inherited roles
	 */
	public static List<RoleType> GreaterRoles(RoleType right) {
		List<RoleType> rights = new LinkedList<RoleType>();
		if (right != null) {
			if (right == ROLE_IDS)
				rights.add(ROLE_IDS);
			else {
				for (int i = right.ordinal() - 1; i < ROLES.length; i++)
					rights.add(ROLES[i]);
			}
		}
		return rights;
	}

}
