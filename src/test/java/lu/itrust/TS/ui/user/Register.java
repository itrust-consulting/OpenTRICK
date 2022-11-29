/**
 * 
 */
package lu.itrust.TS.ui.user;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;
import lu.itrust.TS.ui.tools.BaseUnitTesting;

public class Register extends BaseUnitTesting {

	@Test(groups = { "register", "registerFirst" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void testRegister(String username, String password, String repeatPassword, String firstname, String lastname, String email, String language)
			throws InterruptedException {

		getDriver().get(getBaseUrl() + "/Register");
		//new WebDriverWait(getDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("registerform")));

		sendKeys(findElement(By.id("login")), username);
		sendKeys(findElement(By.id("password")), password);
		sendKeys(findElement(By.id("repeatPassword")), repeatPassword);
		sendKeys(findElement(By.id("firstName")), firstname);
		sendKeys(findElement(By.id("lastName")), lastname);
		sendKeys(findElement(By.id("email")), email);

		new Select(findElement(By.id("locale"))).selectByValue(language);
		click(By.cssSelector("button.btn.btn-primary"));

		//new WebDriverWait(getDriver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form")));
	}
}
