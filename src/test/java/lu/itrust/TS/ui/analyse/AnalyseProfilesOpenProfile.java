package lu.itrust.TS.ui.analyse;

import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;

public class AnalyseProfilesOpenProfile extends BaseAnalyse {

	/**
	 * @param profileName
	 * @throws Exception
	 */
	@Test(groups = { "analyseProfile" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void analyseProfilesOpenProfile(String profileName) throws Exception {

		openProfile(profileName);

		chooseElementInsideDropdown("//a[@href='#tabParameterImpactProba']", false);
		testEditablePage(true, "tabParameterImpactProba");

		chooseElementInsideDropdown("//a[@href='#tabParameterOther']", false);
		testEditablePage(true, "tabParameterOther");

		// Risk analysis - Scenarios
		chooseElementInsideDropdown("//a[@href='#tabScenario']", false);

		// add
		testScenario();
		// Risk treatment / Compliance
		chooseElementInsideDropdown("//a[contains(@onclick,'manageStandard')]", false);

		//fillStandard("Test", "This is a test.", true, "ASSET");
		addStandard("Test", "This is a test.", true, "ASSET");
		addStandard("Testa", "This is a test.", true, "NORMAL");
		addStandard("Testc", "This is a test.", true, "NORMAL");
		
		editStandard("Testc","Testc", "This is a testa.", true, null);

		deleteStandard("Testc");

		int idStandardAssetMeasure = Integer.valueOf(findElement(By.xpath("//div[@id='standardModal']//tbody//tr/td[2 and text() ='Test']/..")).getAttribute("data-trick-id"));
		int idStandardNormalMeasure = Integer.valueOf(findElement(By.xpath("//div[@id='standardModal']//tbody//tr/td[2 and text() ='Testa']/..")).getAttribute("data-trick-id"));

		click(By.xpath("//div[@id='standardModal']//button[@data-dismiss='modal']"));

		// normal measure
		chooseElementInsideDropdown("//a[@href='#tabStandard_" + idStandardNormalMeasure + "']", false);
		testEditablePage(true, "tabStandard_" + idStandardNormalMeasure);

		// asset measure
		chooseElementInsideDropdown("//a[@href='#tabStandard_" + idStandardAssetMeasure + "']", false);
		testEditablePage(true, "tabStandard_" + idStandardAssetMeasure);

		// standard
		chooseElementInsideDropdown("//a[@href='#tabStandard_" + 1 + "']", false);
		testEditablePage(true, "tabStandard_" + 1);
	}

	private void deleteStandard(String name) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='standardModal']//tr/td[2] [text() = '"+name+"']//..//input"));
		click(By.xpath("//a[contains(@onclick,'removeStandard')]"));
		click(By.xpath("//button[@id='deletestandardbuttonYes']"));
	}

	private void editStandard(String name, String newName, String newDescription, boolean newIsComputable,String newStandardTypeValue) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='standardModal']//tr/td[2] [text() = '"+name+"']//..//input"));
		click(By.xpath("//a[@role='edit']"));
		fillStandard(newName,newDescription,newIsComputable,newStandardTypeValue);
		click(By.xpath("//div[@id='standardModal']//button[@name='save']"));
	}

	private void addStandard(String standardName, String standardDescription, Boolean isComputable, String type) throws InterruptedException {
		click(By.xpath("//a[@role='add']"));
		fillStandard(standardName, standardDescription, isComputable, type);
		click(By.name("save"));
	}

	private void testScenario() throws InterruptedException {
		addScenario("Testb", "Confidentiality", "This is a test profile", true, new String[] { "Busi", "Compl" }, 0.25, 0.25, 0.25, 0.25, 1.0, 1.0, 1.0, 1.0, 1.0);
		addScenario("Testc", "Confidentiality", "This is a test profile", true, new String[] { "Busi", "Compl" }, 0.25, 0.25, 0.25, 0.25, 1.0, 1.0, 1.0, 1.0, 1.0);

		Thread.sleep(600);

		selectionCheck("section_scenario", "selectScenario", false, "Testb", false, "Testc", false);
		selectionCheck("section_scenario", "selectScenario", false, "Testb", false, "Testc", true);
		selectionCheck("section_scenario", "selectScenario", false, "Testb", true, "Testc", false);
		selectionCheck("section_scenario", "selectScenario", true, "Testb", false, "Testc", false);
		selectionCheck("section_scenario", "selectScenario", true, "Testb", true, "Testc", false);

		selectionCheck("section_scenario", "selectScenario", false, "Testb", true, "Testc", true);
		selectionCheck("section_scenario", "selectScenario", true, "Testb", true, "Testc", true);

		selectionCheck("section_scenario", "selectScenario", true, "Testb", false, "Testc", true);

		click(By.xpath("//a[@onclick='return editScenario();']"));
		fillScenario("Testc", "Integrity", "Test", 0.3, 0.2, 0.2, 0.3, 1.0, 1.0, 0.0, 1.0, new String[] { });
		click(By.xpath("//button[contains(@onclick,'saveScenario')]"));

		selectionCheck("section_scenario", "selectScenario", false, "Testb", true, "Testc", true);
		click(By.xpath("//div[@id='section_scenario']//a[contains(@onclick,'deleteScenario')]"));
		click(By.xpath("//button[@name='yes']"));
	}

	@Test(groups = { "defaultsChecking" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void defaultsChecking(String profileName1, String profileName2) throws InterruptedException {
		goToProfile();
		// get id of the current standard profile
		String xpathStandard = "//div[@id='section_profile_analysis']//tbody/tr[1]";
		String defaultProfileName = findElement(By.xpath("//div[@id='section_profile_analysis']//tbody/tr[1]/td[2]")).getText();

		// check if those profiles exist
		String xpathElement1 = getProfileRow(profileName1);
		String xpathElement2 = getProfileRow(profileName2);

		assert !xpathElement1.isEmpty() && !xpathElement2.isEmpty();

		// select first then set it as default
		selectCheckBox(true, By.xpath(xpathElement1 + "//input"));
		click(By.xpath("//a[contains(@onclick,'setAsDefaultProfile')]"));

		// try delete the first it should fails
		deleteProfile(false, profileName1);

		// select second then set it as default
		selectCheckBox(true, By.xpath(xpathElement2 + "//input"));
		click(By.xpath("//a[contains(@onclick,'setAsDefaultProfile')]"));

		// try delete it, it should fails
		deleteProfile(false, profileName2);

		// delete first one, it should succeed
		deleteProfile(true, profileName1);

		// set the default to the default profile
		selectCheckBox(true, By.xpath(xpathStandard + "//input"));
		click(By.xpath("//a[contains(@onclick,'setAsDefaultProfile')]"));

		// delete second one, it should succeed
		deleteProfile(true, profileName2);

		// delete default profile it, it should fails.
		deleteProfile(false, defaultProfileName);
	}

	@Test(groups = { "deleteProfile" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void deleteProfile(boolean shouldDelete, String profileName) throws InterruptedException {
		goToProfile();
		selectCheckBox(true, By.xpath(getProfileRow(profileName) + "//input"));
		click(By.xpath("//a[contains(@onclick,'deleteAnalysis')]"));
		click(By.id("deleteanalysisbuttonYes"));
		new WebDriverWait(getDriver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.id("deleteanalysisbuttonYes")));
		getDriver().navigate().refresh();
		assert (findElement(By.xpath(getProfileRow(profileName))) == null && shouldDelete) || (findElement(By.xpath(getProfileRow(profileName))) != null && !shouldDelete);
	}

	@Test(groups = { "detailsProfile" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void detailsProfile(String profileName, String newProfileName, String newLanguage) throws InterruptedException {
		goToProfile();
		selectCheckBox(true, By.xpath(getProfileRow(profileName) + "//input"));
		click(By.xpath("//a[contains(@onclick,'editSingleAnalysis')]"));

		assert !sendKeys(findElement(By.id("analysis_identifier")), "123");
		assert !sendKeys(findElement(By.id("analysis_creationDate")), "123");
		assert !sendKeys(findElement(By.id("analysis_owner")), "123");
		new Select(findElement(By.xpath("//select[@name='analysislanguage']"))).selectByVisibleText(newLanguage);
		assert sendKeys(findElement(By.name("comment")), newProfileName);
		// TODO language change
		click(By.id("editAnalysisButton"));

		getDriver().navigate().refresh();

		assert findElement(By.xpath(getProfileRow(newProfileName))) != null;
	}

	@Test(groups = { "checkIfProfileExist" })
	public String getProfileRow(String profileName) throws InterruptedException {
		goToProfile();
		String xpath = "//div[@id='section_profile_analysis']//tbody/tr/td[2][text() = '" + profileName + "']/..";
		return findElement(By.xpath(xpath)) != null ? xpath : "";
	}

	@Test(groups = { "newProfile" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void newProfile(String companyName, String profileName) throws InterruptedException {
		goToAllAnalysis(companyName, null);
		// random string
		String analyseName;
		do {
			analyseName = UUID.randomUUID().toString();
		} while (isElementPresent(By.xpath("//div[@id='section_analysis']//tbody//td[2 and string() = '" + analyseName + "']")));
		// new analyse
		addAnalysis(companyName, null, null, "123", "0.1", analyseName, "1", false, false);
		// select it
		selectAnalysis(analyseName);
		// new profile click name it and choose which standards
		click(By.xpath("//a[contains(@onclick,'createAnalysisProfile')]"));
		sendKeys(findElement(By.id("name")), profileName);

		click(By.xpath("//div[@id='analysisProfileModal']//button[contains(@onclick,'saveAnalysisProfile')]"));

		new WebDriverWait(getDriver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='analysisProfileModal']//button[contains(@onclick,'saveAnalysisProfile')]")));

		// delete analyse
		deleteAnalysis(companyName, analyseName);
		// check if profile was create by going to knowledge base
		goToKnowledgeBase();
		click(By.xpath("//a[@hreF='#tab_analyses']"));

		assert findElement(By.xpath("(//div[@id='section_profile_analysis']//tbody/tr/td[2])/.[text() ='" + profileName + "']")) != null;
	}

	public void openProfile(String profileName) throws InterruptedException {
		goToProfile();
		selectCheckBox(true, By.xpath("//div[@id='section_profile_analysis']//tbody/tr/td[2][text() = '" + profileName + "']/..//input"));
		click(By.xpath("//a[contains(@onclick,'selectAnalysis')]"));
	}

	protected void goToProfile() throws InterruptedException {
		goToKnowledgeBase();
		click(By.xpath("//a[@href='#tab_analyses']"));
	}

}
