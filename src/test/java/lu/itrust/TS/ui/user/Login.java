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
		getDriver().get(getBaseUrl() + "/Login");
		new WebDriverWait(getDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("login_signin_button")));
		sendKeys(findElement(By.id("username")), username);
		sendKeys(findElement(By.name("password")), password);
		click(By.id("login_signin_button"));
		
		Thread.sleep(600);
		if (findElement(By.id("wrap"))!=null){
			getDriver().get(getBaseUrl());
		}
		
	}

}
