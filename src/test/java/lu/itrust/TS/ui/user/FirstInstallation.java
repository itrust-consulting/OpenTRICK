package lu.itrust.TS.ui.user;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;
import lu.itrust.TS.ui.tools.BaseUnitTesting;

public class FirstInstallation extends BaseUnitTesting {

	@Test(groups = { "firstInstallation" })
	public void firstInstallation() throws Exception {
		goToAdministration();
		click(By.xpath("//a[@onclick='return installTrickService();']"));
		//new WebDriverWait(getDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("task-manager")));
		//new WebDriverWait(getDriver(), 60).until(ExpectedConditions.invisibilityOfElementLocated(By.id("task-manager")));
	}

	@Test(groups = { "firstInstallationAddCustomer" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	private void addCustomer(String company, String contactPerson, String phoneNumber, String emailAddress, String address, String city, String zipCode, String country)
			throws InterruptedException {
		goToAdministration();
		click(By.xpath("//a[@href='#tab_customer']"));
		click(By.xpath("//a[@onclick='return newCustomer();']"));

		sendKeys(findElement(By.id("customer_organisation")), company);
		sendKeys(findElement(By.id("customer_contactPerson")), contactPerson);
		sendKeys(findElement(By.id("customer_phoneNumber")), phoneNumber);
		sendKeys(findElement(By.id("customer_email")), emailAddress);
		sendKeys(findElement(By.id("customer_address")), address);
		sendKeys(findElement(By.id("customer_city")), city);
		sendKeys(findElement(By.id("customer_ZIPCode")), zipCode);
		sendKeys(findElement(By.id("customer_country")), country);

		click(By.id("addcustomerbutton"));
	}

	@Test(groups = { "firstInstallationAddUser" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	private void addUser(String username, String password, String firstName, String lastName, String emailAddress, String[] roles) throws Exception {
		goToAdministration();
		click(By.xpath("//a[@href='#tab_user']"));
		click(By.xpath("//a[@onclick='return newUser();']"));

		//new WebDriverWait(getDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("user_login")));

		sendKeys(findElement(By.id("user_login")), username);
		sendKeys(findElement(By.id("user_password")), password);
		sendKeys(findElement(By.id("user_firstName")), firstName);
		sendKeys(findElement(By.id("user_lastName")), lastName);
		sendKeys(findElement(By.id("user_email")), emailAddress);

		for (String role : roles) {
			click(By.id(role));
		}
		click(By.id("addUserbutton"));
		click(By.className("close"));
	}

	private void goToAdministration() throws InterruptedException {
		click(By.xpath("//a[substring-before(@href,'/Admin')]"));
	}

}
