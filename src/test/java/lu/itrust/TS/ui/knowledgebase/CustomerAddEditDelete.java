package lu.itrust.TS.ui.knowledgebase;

import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.BaseUnitTesting;

public class CustomerAddEditDelete extends BaseUnitTesting {

	@Parameters(value = { "organisation", "contactPerson", "phoneNumber", "email", "ZIPCode", "city", "country",
			"address", "editOrganisation", "editContactPerson", "editPhoneNumber", "editEmail", "editZIPCode",
			"editCity", "editCountry", "editAddress", })
	@Test(groups = { "customerAddEditDelete" })
	public void testCustomerAddEditDelete(String organisation, String contactPerson, String phoneNumber, String email,
			String ZIPCode, String city, String country, String address,

			String editOrganisation, String editContactPerson, String editPhoneNumber, String editEmail,
			String editZIPCode, String editCity, String editCountry, String editAddress) throws InterruptedException {
		goTabCustomers();
		// add customer
		addCustomer(organisation, contactPerson, phoneNumber, email, ZIPCode, city, country, address);

		// edit
		updateCustomer(organisation, editOrganisation, editContactPerson, editPhoneNumber, editEmail, editZIPCode,
				editCity, editCountry, editAddress);

		// delete
		deleteCustomer(editOrganisation);

	}

	@Parameters(value = { "organisation", "contactPerson", "phoneNumber", "email", "ZIPCode", "city", "country",
			"address" })
	@Test(groups = { "addCustomer" })
	public void addCustomer(String organisation, String contactPerson, String phoneNumber, String email, String ZIPCode,
			String city, String country, String address) throws InterruptedException {
		goTabCustomers();
		// add customer
		click(By.xpath("//a[contains(@onclick,'newCustomer()')]"));

		fillForm(organisation, contactPerson, phoneNumber, email, ZIPCode, city, country, address);

		click(By.id("addcustomerbutton"));

		Thread.sleep(600);
		assert !isElementPresent(By.xpath("//form[@id='customer_form']//label[contains(@class,'label-danger')]"));
	}

	@Parameters(value = { "updateThisOrganisation", "organisation", "contactPerson", "phoneNumber", "email", "ZIPCode",
			"city", "country", "address" })
	@Test(groups = { "updateCustomer" })
	public void updateCustomer(String updateThisOrganisation, String organisation, String contactPerson,
			String phoneNumber, String email, String ZIPCode, String city, String country, String address)
			throws InterruptedException {
		goTabCustomers();
		// update
		selectCheckBoxCustomer(updateThisOrganisation);

		click(By.xpath("//a[contains(@onclick,'editSingleCustomer()')]"));

		fillForm(organisation, contactPerson, phoneNumber, email, ZIPCode, city, country, address);

		click(By.id("addcustomerbutton"));
		assert !isElementPresent(By.xpath("//form[@id='customer_form']//label[contains(@class,'label-danger')]"));
	}

	@Parameters(value = { "deleteThisCustomer" })
	@Test(groups = { "deleteCustomer" })
	public void deleteCustomer(String deleteThisCustomer) throws InterruptedException {
		// update
		goTabCustomers();
		selectCheckBoxCustomer(deleteThisCustomer);

		click(By.xpath("//a[contains(@onclick,'deleteCustomer()')]"));
		click(By.id("deletecustomerbuttonYes"));

		assert !isElementPresent(By.xpath("//form[@id='customer_form']//label[contains(@class,'label-danger')]"));
	}

	private void selectCheckBoxCustomer(String customer) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='section_customer']//td[@data-trick-name='organisation'][ text() = '"
				+ customer + "']/..//input"));
	}

	private void goTabCustomers() throws InterruptedException {
		goToKnowledgeBase();
		click(By.xpath("//a[@href='#tab_customer']"));
	}

	private void fillForm(String organisation, String contactPerson, String phoneNumber, String email, String ZIPCode,
			String city, String country, String address) {
		sendKeys(findElement(By.id("customer_organisation")), organisation);
		sendKeys(findElement(By.id("customer_contactPerson")), contactPerson);

		sendKeys(findElement(By.id("customer_phoneNumber")), phoneNumber);
		sendKeys(findElement(By.id("customer_email")), email);
		sendKeys(findElement(By.id("customer_ZIPCode")), ZIPCode);
		sendKeys(findElement(By.id("customer_city")), city);
		sendKeys(findElement(By.id("customer_country")), country);
		sendKeys(findElement(By.id("customer_address")), address);
	}
}
