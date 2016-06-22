/**
 * 
 */
package lu.itrust.TS.ui.user;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.BaseUnitTesting;

public class Register extends BaseUnitTesting {

	@Parameters(value = { "username", "password", "repeatPassword", "firstname", "lastname", "email", "language" })
	@Test(groups = { "register", "registerFirst" })
	public void testRegister(String username, String password, String repeatPassword, String firstname, String lastname,
			String email, String language) throws Exception {
		getDriver().get(getBaseUrl() + "/Register");
		assert isElementPresent(By.name("registerform"));

		sendKeys(findElement(By.id("login")), username);
		sendKeys(findElement(By.id("password")), password);
		sendKeys(findElement(By.id("repeatPassword")), repeatPassword);
		sendKeys(findElement(By.id("firstName")), firstname);
		sendKeys(findElement(By.id("lastName")), lastname);
		sendKeys(findElement(By.id("email")), email);

		new Select(findElement(By.id("locale"))).selectByValue(language);
		click(By.cssSelector("button.btn.btn-primary"));
		Thread.sleep(1000);
		assert !isElementPresent(By.cssSelector("label.label.label-danger"));
		assert isElementPresent(By.id("login_form"));
		assert isElementPresent(By.id("success"));
	}
}
