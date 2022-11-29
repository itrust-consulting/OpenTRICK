package lu.itrust.TS.ui.administration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;
import lu.itrust.TS.ui.tools.BaseUnitTesting;

public class ChangeAdministrationSettings extends BaseUnitTesting {

	@Test(groups = { "testChangeSettingsReset" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void testChangeSettingsReset(String username, String password) throws Exception {
		// settings
		testSettings(username, password, "//a[contains(@href,'/ResetPassword')]", "#SETTING_ALLOWED_RESET_PASSWORD");
	}

	@Test(groups = { "testToogleSignUpSetting" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void testToogleSignUpSetting(String username, String password) throws Exception {
		// settings
		testSettings(username, password, "//a[contains(@href,'/Register')]", "#SETTING_ALLOWED_SIGNUP");
	}

	@Test(groups = { "testToogleTicketsSetting" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void testToogleTicketsSetting(String username, String password) throws Exception {
		login(username, password);
		// init xpath variables
		String xpathCheckboxesId = "#SETTING_ALLOWED_TICKETING_SYSTEM_LINK";
		By byCheckboxInput = By.cssSelector(xpathCheckboxesId + " .btn-group :not(.active) input");

		click(By.xpath("//a[substring-before(@href,'/Admin')]"));
		click(By.xpath("//a[@href='#tab_tsSetting']"));

		String stateBefore = findElement(byCheckboxInput).getAttribute("value");

		click(By.cssSelector(xpathCheckboxesId + " .btn-group label:not(.active)"));
		
		getDriver().navigate().refresh();

		assert !findElement(byCheckboxInput).getAttribute("value").equals(stateBefore);

	}

	private void testSettings(String username, String password, String xpathState, String toogleSelector) throws InterruptedException {
		signOut();
		getDriver().get(getBaseUrl() + "/Login");
		boolean state = isElementPresent(By.xpath(xpathState));
		login(username, password);

		click(By.xpath("//a[substring-before(@href,'/Admin')]"));

		click(By.xpath("//a[contains(@href,'tab_tsSetting')]"));

		click(By.cssSelector(toogleSelector + " .btn-group label:not(.active)"));

		signOut();

		//new WebDriverWait(getDriver(), 10).until(ExpectedConditions.presenceOfElementLocated(By.id("login_form")));

		assert isElementPresent(By.xpath(xpathState)) != state;
	}

}