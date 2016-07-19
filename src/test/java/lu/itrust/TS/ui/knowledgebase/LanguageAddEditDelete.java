package lu.itrust.TS.ui.knowledgebase;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;
import lu.itrust.TS.ui.tools.BaseUnitTesting;

public class LanguageAddEditDelete extends BaseUnitTesting {

	@Test(groups = { "addLanguage" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void addLanguage(String alpha3, String name, String altName) throws InterruptedException {
		goToTabLanguage();
		assert !isElementPresent(By.xpath("//a[contains(@onclick,'newLanguage()')]/..[@class='disabled']"));

		click(By.xpath("//a[contains(@onclick,'newLanguage()')]"));

		fillForm(alpha3, name, altName);

		click(By.id("addlanguagebutton"));
		assert !isElementPresent(By.cssSelector("label.label.label-danger"));
	}

	@Test(groups = { "deleteLanguage" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void deleteLanguage(String deleteThisAlpha3) throws InterruptedException {
		goToTabLanguage();
		selectLanguage(deleteThisAlpha3);

		click(By.xpath("//a[contains(@onclick,'deleteLanguage()')]"));
		click(By.id("deletelanguagebuttonYes"));

		assert !isElementPresent(By.cssSelector("label.label.label-danger"));
	}

	@Test(groups = { "updateLanguage" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void updateLanguage(String updateThisAlpha3, String alpha3, String name, String altName) throws InterruptedException {
		goToTabLanguage();
		selectLanguage(updateThisAlpha3);
		click(By.xpath("//a[contains(@onclick,'editSingleLanguage()')]"));
		fillForm(alpha3, name, altName);
		click(By.id("addlanguagebutton"));
		assert !isElementPresent(By.cssSelector("label.label.label-danger"));
	}

	private void fillForm(String alpha3, String name, String altName) {
		sendKeys(findElement(By.id("language_alpha3")), alpha3);
		sendKeys(findElement(By.id("language_name")), name);
		sendKeys(findElement(By.id("language_altName")), altName);
	}

	private void goToTabLanguage() throws InterruptedException {
		goToKnowledgeBase();
		click(By.xpath("//a[@href='#tab_language']"));
	}

	private void selectLanguage(String alpha3) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='tab_language']//td[@data-field-name='alpha3'][ text() = '" + alpha3 + "']/..//input"));
	}
}
