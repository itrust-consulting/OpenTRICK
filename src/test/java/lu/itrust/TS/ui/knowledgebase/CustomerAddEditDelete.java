package lu.itrust.TS.ui.knowledgebase;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;
import lu.itrust.TS.ui.tools.BaseUnitTesting;

public class CustomerAddEditDelete extends BaseUnitTesting {


	@Test(groups = { "addCustomer" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
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

	@Test(groups = { "deleteCustomer" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void deleteCustomer(String deleteThisCustomer) throws InterruptedException  {
		// update
		goTabCustomers();
		selectCheckBoxCustomer(deleteThisCustomer);

		click(By.xpath("//a[contains(@onclick,'deleteCustomer()')]"));
		click(By.id("deletecustomerbuttonYes"));

		assert !isElementPresent(By.xpath("//form[@id='customer_form']//label[contains(@class,'label-danger')]"));
	}

	
	@Test(groups = { "updateCustomer" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void updateCustomer(String updateThisOrganisation, String organisation, String contactPerson,
			String phoneNumber, String email, String ZIPCode, String city, String country, String address) throws InterruptedException {
		goTabCustomers();
		// update
		selectCheckBoxCustomer(updateThisOrganisation);
		
		click(By.xpath("//a[contains(@onclick,'editSingleCustomer()')]"));

		fillForm(organisation, contactPerson, phoneNumber, email, ZIPCode, city, country, address);

		click(By.id("addcustomerbutton"));
		assert !isElementPresent(By.xpath("//form[@id='customer_form']//label[contains(@class,'label-danger')]"));
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

	
	private void goTabCustomers() throws InterruptedException {
		goToKnowledgeBase();
		click(By.xpath("//a[@href='#tab_customer']"));
	}

	private void selectCheckBoxCustomer(String customer) throws InterruptedException{
		selectCheckBox(true, By.xpath("//div[@id='section_customer']//td[@data-trick-name='organisation'][ text() = '"
				+ customer + "']/..//input"));
	}
}
