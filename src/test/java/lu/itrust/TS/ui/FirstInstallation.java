package lu.itrust.TS.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class FirstInstallation extends BaseUnitTesting {
	@Test(groups = { "firstInstallation" })
	public void firstInstallation() throws Exception {
		// reinstall trickservice
		click(By.xpath("//a[@href='/trickservice/Admin']"));
		click(By.xpath("//a[@onclick='return installTrickService();']"));

		Thread.sleep(1500);

		//
		WebDriverWait wait = new WebDriverWait(getDriver(), 60);

		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//ul[@id='task-manager']")));

		assert !isElementPresent(By.xpath("//ul[@id='task-manager']"));

		click(By.xpath("//a[@href='#tab_user']"));

		// Add a user (consultation)
		String[] roles = new String[1];
		roles[0] = "ROLE_CONSULTANT";

		addUser("deimosa", "Qwertz12", "deimos", "alpha", "deimos.aplha@test.de", roles);

		// Add a user (user)
		roles[0] = "ROLE_USER";

		addUser("deimosb", "Qwertz12", "deimos", "beta", "deimos.beta@test.de", roles);
		// Add a user (supervisor)
		roles[0] = "ROLE_SUPERVISOR";

		addUser("deimosc", "Qwertz12", "deimos", "creos", "deimos.creos@test.de", roles);
		// Add a customer
		click(By.xpath("//a[@href='#tab_customer']"));
		addCustomer("a", "Test a", "1234", "123@test.de", "123", "lux", "1234", "Luxembourg");
	}

	private void addCustomer(String company, String contactPerson, String phoneNumber, String emailAddress,
			String address, String city, String zipCode, String country) throws InterruptedException {

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

	private void addUser(String username, String password, String firstName, String lastName, String emailAddress,
			String[] roles) throws Exception {
		click(By.xpath("//a[@onclick='return newUser();']"));

		WebDriverWait wait = new WebDriverWait(getDriver(), 1);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_login")));

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
}
