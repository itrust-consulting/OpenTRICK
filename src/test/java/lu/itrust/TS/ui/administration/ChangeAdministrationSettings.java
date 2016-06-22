package lu.itrust.TS.ui.administration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.BaseUnitTesting;

public class ChangeAdministrationSettings extends BaseUnitTesting {

	@Parameters(value = { "username", "password" })
	@Test(groups = { "testToogleTicketsSetting" })
	public void testToogleTicketsSetting(String username, String password) throws Exception {
		login(username, password);
		// settings
		String selector = "#SETTING_ALLOWED_TICKETING_SYSTEM_LINK";
		String searchCssState = selector + " .btn-group :not(.active) input";

		click(By.xpath("//a[substring-before(@href,'/Admin')]"));
		click(By.xpath("//a[@href='#tab_tsSetting']"));

		String stateBefore = findElementByCss(searchCssState).getAttribute("value");

		click(By.cssSelector(selector + " .btn-group label:not(.active)"));

		assert !findElementByCss(searchCssState).getAttribute("value").equals(stateBefore);

	}

	@Parameters(value = { "username", "password" })
	@Test(groups = { "testToogleSignUpSetting" })
	public void testToogleSignUpSetting(String username, String password) throws Exception {
		// settings
		testSettings(username, password, "//a[contains(@href,'/Register')]", "#SETTING_ALLOWED_SIGNUP");
	}

	@Parameters(value = { "username", "password" })
	@Test(groups = { "testChangeSettingsReset" })
	public void testChangeSettingsReset(String username, String password) throws Exception {
		// settings
		testSettings(username, password, "//a[contains(@href,'/ResetPassword')]", "#SETTING_ALLOWED_RESET_PASSWORD");
	}

	private void testSettings(String username, String password, String xpathState, String toogleSelector)
			throws InterruptedException {
		String searchXpathState = xpathState;
		getDriver().get(getBaseUrl() + "/Login");
		boolean state = isElementPresent(By.xpath(searchXpathState));
		login(username, password);
		String selector = toogleSelector;

		click(By.xpath("//a[substring-before(@href,'/Admin')]"));

		click(By.xpath("//a[contains(@href,'tab_tsSetting')]"));

		click(By.cssSelector(selector + " .btn-group label:not(.active)"));

		signOut();

		new WebDriverWait(getDriver(), 25).until(ExpectedConditions.presenceOfElementLocated(By.id("login_form")));

		assert isElementPresent(By.xpath(searchXpathState)) != state;
	}

}