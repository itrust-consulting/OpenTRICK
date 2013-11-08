package TS;
import junit.framework.TestCase;
import lu.itrust.business.view.model.Role;
import lu.itrust.business.view.model.RoleType;
import lu.itrust.business.view.model.User;

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
		
		user.add(new Role(RoleType.ROLE_ADMIN));
		
		//user.add(new Role(RoleType.ROLE_USER));
		
		
		System.out.println(RoleType.ROLE_USER.ordinal());
		
		System.out.println(RoleType.ROLE_CONSULTANT.ordinal());
		
		System.out.println(RoleType.ROLE_ADMIN.ordinal());
		
		System.out.println(RoleType.ROLE_SUPERVISOR.ordinal());
		
		assertTrue("ROLE_USER<ROLE_CONSULTANT", user.isAutorise("ROLE_CONSULTANT"));
		
	}
	

}
