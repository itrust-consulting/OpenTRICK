package lu.itrust.TS.ui.user;

import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.BaseUnitTesting;

public class Login extends BaseUnitTesting {

	@Parameters(value = { "username", "password" })
	@Test(groups = { "login", "loginFirst" })
	public void testLogin(String username, String password) throws InterruptedException {
		getDriver().get(getBaseUrl() + "/Login");
		assert isElementPresent(By.id("login_signin_button"));
		sendKeys(findElement(By.id("username")), username);
		sendKeys(findElement(By.name("password")), password);
		click(By.id("login_signin_button"));

		assert !isElementPresent(By.id("login"));
		getDriver().get(getBaseUrl());
	}

}
