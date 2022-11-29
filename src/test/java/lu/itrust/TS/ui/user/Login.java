package lu.itrust.TS.ui.user;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;
import lu.itrust.TS.ui.tools.BaseUnitTesting;

public class Login extends BaseUnitTesting {

	@Test(groups = { "login", "loginFirst" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void testLogin(String username, String password) throws InterruptedException {
		getDriver().get(getBaseUrl() + "/Home");
		//new WebDriverWait(getDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("login_signin_button")));
		sendKeys(findElement(By.id("username")), username);
		sendKeys(findElement(By.name("password")), password);
		click(By.id("login_signin_button"));
		//new WebDriverWait(getDriver(), 10).until(ExpectedConditions.invisibilityOfElementLocated(By.id("login_signin_button")));
		if (!getDriver().getCurrentUrl().endsWith("/Home"))
			getDriver().get(getBaseUrl() + "/Home");
	}

}
