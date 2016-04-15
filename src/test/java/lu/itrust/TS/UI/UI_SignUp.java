package lu.itrust.TS.UI;
import static org.junit.Assert.assertEquals;

import org.testng.annotations.Test;

@Test(groups="SignUp")
public class UI_SignUp extends UI_Initialisation {

	@Test
	public void testBrowser() {

	
		
		// And now use this to visit Google
		this.driver.get("http://localhost:8088 ");

		System.out.println(driver.getTitle());

		assertEquals("Title is not right", "Sign in", driver.getTitle());

		driver.manage().deleteAllCookies();

	}
}
