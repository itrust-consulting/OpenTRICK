package lu.itrust.TS.ui.user;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.BaseUnitTesting;

public class ResetPassword extends BaseUnitTesting {

	@Parameters(value = { "username" })
	@Test(groups = { "resetPassword" })
	public void testResetPasswordCase(String username) throws Exception {
		getDriver().get(getBaseUrl() + "/ts-ut/ResetPassword");
		assertTrue(isElementPresent(By.id("resetPassword")));

		sendKeys(findElement(By.id("username")), username);
		click(By.xpath("//form[@id='resetPassword']//button[@type='submit']"));
		Thread.sleep(30);
		assertFalse(isElementPresent(By.cssSelector("label.label.label-danger")));
		assertFalse(isElementPresent(By.id("login-form")));
		assertTrue(isElementPresent(By.id("success")));
	}

}