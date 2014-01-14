package TS;
import junit.framework.TestCase;
import lu.itrust.business.TS.usermanagment.Role;
import lu.itrust.business.TS.usermanagment.RoleType;
import lu.itrust.business.TS.usermanagment.User;

/**
 * 
 */

/**
 * @author oensuifudine
 *
 */
public class UserTest extends TestCase {
	
	public void testIsAutorise(){
		
		
		User user = new User();
		
		user.addRole(new Role(RoleType.ROLE_ADMIN));
		
		//user.add(new Role(RoleType.ROLE_USER));
		
		
		System.out.println(RoleType.ROLE_USER.ordinal());
		
		System.out.println(RoleType.ROLE_CONSULTANT.ordinal());
		
		System.out.println(RoleType.ROLE_ADMIN.ordinal());
		
		System.out.println(RoleType.ROLE_SUPERVISOR.ordinal());
		
		assertTrue("ROLE_USER<ROLE_CONSULTANT", user.isAutorised("ROLE_CONSULTANT"));
		
	}
	

}
