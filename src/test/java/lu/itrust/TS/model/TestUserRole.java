package lu.itrust.TS.model;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lu.itrust.business.TS.usermanagement.RoleType;

public class TestUserRole {

	@Test
	public void testUserAuthorisation() {
		
		List<RoleType> roles = RoleType.InheritedRoles(RoleType.ROLE_USER);

		Assert.assertTrue("Size must be 1", roles.size() == 1);

		Assert.assertTrue("User role cannot be found", roles.stream().allMatch(role -> role == RoleType.ROLE_USER));
		
		roles = RoleType.InheritedRoles(RoleType.ROLE_CONSULTANT);
		
		Assert.assertTrue("Size must be 2", roles.size() == 2);
		
		Assert.assertTrue("User role cannot be found", roles.stream().anyMatch(role -> role == RoleType.ROLE_USER));
		
		Assert.assertTrue("Consultant role cannot be found", roles.stream().anyMatch(role -> role == RoleType.ROLE_CONSULTANT));
	}

}
