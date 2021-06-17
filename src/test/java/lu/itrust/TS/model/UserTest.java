package lu.itrust.TS.model;
import org.junit.Assert;
import org.junit.Test;

import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author oensuifudine
 *
 */
public class UserTest {
	
	@Test
	public void testIsAutorise(){
		
		User user = new User();
		
		user.addRole(new Role(RoleType.ROLE_ADMIN));
		
		System.out.println(RoleType.ROLE_USER.ordinal());
		
		System.out.println(RoleType.ROLE_CONSULTANT.ordinal());
		
		System.out.println(RoleType.ROLE_ADMIN.ordinal());
		
		System.out.println(RoleType.ROLE_SUPERVISOR.ordinal());
		
		Assert.assertTrue("ROLE_USER<ROLE_CONSULTANT", user.isAutorised("ROLE_CONSULTANT"));
		
	}
	

}
