package lu.itrust.TS.ui.analyse;

import java.util.ArrayList;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;

public class Analyse extends BaseAnalyse {

	@Test(groups = { "addAnalysis" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	@Override
	public void addAnalysis(String company, String language, String profile, String author, String version, String name,
			String description, boolean isUncertainty, boolean isCSSF) throws InterruptedException {
		super.addAnalysis(company, language, profile, author, version, name, description, isUncertainty, isCSSF);
	}

	@Test(groups = { "deleteAnalysis" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	@Override
	public void deleteAnalysis(String companyName, String analyseName) throws InterruptedException {
		super.deleteAnalysis(companyName, analyseName);
	}

	@Test(groups = { "editAnalysis" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void editAnalysis(String defaultCompanyName, String defaultAnalyseName, String newAnalyseName,
			String newCompanyName, String language, boolean isUncertainty, boolean isCSSF) throws InterruptedException {
		goToAllAnalysis(defaultCompanyName, defaultAnalyseName);
		click(By.xpath("//a[contains(@onclick,'editSingleAnalysis')]"));

		assert !sendKeys(findElement(By.id("analysis_identifier")), "");
		assert !sendKeys(findElement(By.id("analysis_version")), "");
		assert !sendKeys(findElement(By.id("analysis_creationDate")), "");
		assert !sendKeys(findElement(By.id("analysis_owner")), "");
		assert !findElement(By.id("analysis_hasData")).isEnabled();

		new Select(findElement(By.xpath("//div[@id='analysiscustomercontainer']//select")))
				.selectByVisibleText(newCompanyName);
		new Select(findElement(By.xpath("//div[@id='analysislanguagecontainer']//select")))
				.selectByVisibleText(language);

		sendKeys(findElement(By.xpath("//input[contains(@name,'comment')]")), newAnalyseName);

		selectCheckBox(isUncertainty, By.name("uncertainty"));
		selectCheckBox(isCSSF, By.name("cssf"));

		click(By.xpath("//div[@id='editAnalysisModel']//button[@id='editAnalysisButton']"));

		goToAllAnalysis(newCompanyName, newAnalyseName);
	}

	@Test(groups = { "analyse" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void testAnalyse(String companyName, String analyseName) throws Exception {
		String newValue = "";

		goToAllAnalysis(companyName, analyseName);

		// edit
		click(By.xpath(
				"//li[contains(@data-trick-check,\"hasRight('MODIFY')\")]/a[contains(@onclick,'selectAnalysis')]"));

		testDefaultEditable(true);
		// version
		testVersion(companyName, analyseName, newValue);
		// test asset
		testAsset();
		// scenario
		testScenarioPart();
		// estimation
		testRisksheets();
		// get all standard id
		testStandard();

		// TODO Phases add/edit/delete

		// action plan compute
		testActionPlanCompute();

		if (findElement(By.xpath("//a[contains(@href,'#tabStandard_') and text()='27002']")) != null) {
			chooseElementInsideDropdown("//a[@href='#tabSOA']", false);
			testEditablePage(true, "tabSOA");
		}
		chooseElementInsideDropdown("//a[@href='#tabSummary']", false);

		testRRF();
	}

	private void testActionPlanCompute() throws InterruptedException {
		chooseElementInsideDropdown("//a[@href='#tabActionPlan']", false);
		click(By.xpath("//ul[@id='menu_actionplans']//a[contains(@onclick,'displayActionPlanOptions')]"));
		click(By.xpath("//button[@id='computeActionPlanButton']"));
		new WebDriverWait(getDriver(), 30).until(
				ExpectedConditions.invisibilityOfElementLocated(By.xpath("//span[@id='task-counter' and text()='1']")));
		waitLoadingIndicator();
	}

	private void testRRF() throws InterruptedException {
		chooseElementInsideDropdown("//a[contains(@onclick,'return loadRRF();')]", false);

		// get all scenarios
		String xpathScenario = "//div[@id='selectable_rrf_scenario_controls']//a[@data-trick-class]";
		for (int i = 1; i <= findElements(By.xpath(xpathScenario)).size(); i++) {
			// get the current scenario
			By byScenario = By.xpath("(" + xpathScenario + ")[" + i + "]");
			// get the class of the scenario
			// String scenarioType =
			// findElement(byScenario).getAttribute("data-trick-class");
			// click on the actual scenarios
			click(byScenario);

			// exploring chapters
			for (int chapterIndex = 0; chapterIndex < new Select(findElement(By.name("chapterselection"))).getOptions()
					.size(); chapterIndex++) {
				selectComboboxByIndex(By.name("chapterselection"), chapterIndex);
				// have fun with the measures xD
			}
		}
	}

	private void testStandard() throws InterruptedException, Exception {
		chooseElementInsideDropdown("//a[contains(@onclick,'manageStandard')]", false);
		ArrayList<String> standardids = new ArrayList<>();
		findElements(By.xpath("//div[@id='standardModal']//table[@id='table_current_standard']//tbody/tr"))
				.forEach((element) -> {
					if (element.findElement(By.xpath("//td[2]")).getText().equals("27002")) {
						standardids.add(0, element.getAttribute("data-trick-id"));
					} else {
						standardids.add(element.getAttribute("data-trick-id"));
					}
				});
		for (int i = 1; i < standardids.size(); i++) {
			selectCheckBox(true,
					By.xpath("//div[@id='standardModal']//tr[@data-trick-id='" + standardids.get(i) + "']//input"));
			click(By.xpath("//a[contains(@onclick,'removeStandard')]"));
			click(By.xpath("//button[@id='deletestandardbuttonYes']"));
		}

		click(By.xpath("//div[@id='standardModal']//button[@name='cancel']"));
		chooseElementInsideDropdown("//a[@href='#" + "tabStandard_" + standardids.get(0) + "']", false);
		testEditablePage(true, "tabStandard_" + standardids.get(0));
	}

	private void testRisksheets() throws InterruptedException {
		String oldValue;
		String newValue;
		chooseElementInsideDropdown("//a[@href='?open=edit-estimation']", false);
		click(By.xpath(
				"//div[@role='left-menu']//div[@class='list-group']//a[contains(@class,'active')]/following-sibling::a[1]"));

		// edit description
		click(By.xpath("//div[@id='estimation-ui']//i[contains(@class,'fa')]"));

		sendKeys(findElement(By.id("description")), "Test");

		click(By.xpath("//div[@id='estimation-ui']//i[contains(@class,'fa')]"));

		click(By.xpath("//button[@name='impactScale']"));
		Thread.sleep(600);
		ArrayList<String> acronyms = new ArrayList<>();

		for (WebElement acronymElement : getDriver().findElements(By.xpath("//div[@id='impactScale']//tr/td[2]"))) {
			acronyms.add(acronymElement.getText());
		}
		click(By.xpath("//div[@id='impactScale']//*[@data-dismiss='modal']"));
		Thread.sleep(600);

		oldValue = findElement(By.name("impactFin")).getText();

		newValue = acronyms.get(new Random().nextInt(acronyms.size()));
		sendKeys(findElement(By.name("impactFin")), newValue);
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].focus();arguments[0].blur();return true",
				findElement(By.name("impactFin")));

		assert !isElementPresent(By.xpath("//input[@name='impactFin']/..[contains(@class,'has-error')]"));

		Select probability = new Select(findElement(By.xpath("//select[@name='likelihood']")));
		probability.selectByIndex(new Random().nextInt(probability.getOptions().size()));

		oldValue = findElement(By.xpath("//input[@name='owner']")).getAttribute("value");
		if (oldValue.equals("test")) {
			sendKeys(findElement(By.xpath("//input[@name='owner']")), "testa");
		} else {
			sendKeys(findElement(By.xpath("//input[@name='owner']")), "test");
		}

		assert !oldValue.equals(findElement(By.xpath("//input[@name='owner']")).getAttribute("value"));

		// comment
		oldValue = findElement(By.xpath("//textarea[@name='comment']")).getText();
		if (oldValue.equals("test")) {
			sendKeys(findElement(By.xpath("//textarea[@name='comment']")), "testa");
		} else {
			sendKeys(findElement(By.xpath("//textarea[@name='comment']")), "test");
		}

		// hidden comment
		oldValue = findElement(By.xpath("//textarea[@name='hiddenComment']")).getText();
		if (oldValue.equals("test")) {
			sendKeys(findElement(By.xpath("//textarea[@name='hiddenComment']")), "testa");
		} else {
			sendKeys(findElement(By.xpath("//textarea[@name='hiddenComment']")), "test");
		}

		// finish estimation
		click(By.xpath("//a[@data-base-ul='?open=edit']"));
	}

	private void testScenarioPart() throws InterruptedException, Exception {
		chooseElementInsideDropdown("//a[@href='#tabScenario']", false);

		addScenario("Test", "Confidentiality", "This is a test profile", true, new String[] { "SW", "HW" }, 0.25, 0.25,
				0.25, 0.25, 1.0, 1.0, 1.0, 1.0, 1.0);
		addScenario("Testa", "Confidentiality", "This is a test profile", true, new String[] { "SW" }, 0.15, 0.25, 0.3,
				0.3, 1.0, 0.1, 1.0, 0.1, 1.0);
		addScenario("Testb", "Confidentiality", "This is a test profile", true, new String[] { "HW" }, 0.15, 0.25, 0.3,
				0.3, 1.0, 0.1, 1.0, 0.1, 1.0);

		selectCheckBox(true, By.xpath(
				"//div[@id='tabScenario']//table[@id='scenariotable']//tbody/tr/td[3 and text() = 'Test']/..//input"));
		click(By.xpath("//div[@id='tabScenario']//a[contains(@onclick,'showEstimation')]"));
		testEditablePage(true, "section_scenario_assessment");

		selectCheckBox(true, By.xpath(
				"//div[@id='tabScenario']//table[@id='scenariotable']//tbody/tr/td[3 and text() = 'Testa']/..//input"));
		click(By.xpath("//div[@id='tabScenario']//a[contains(@onclick,'showEstimation')]"));
		testEditablePage(true, "section_scenario_assessment");

		selectCheckBox(true, By.xpath(
				"//div[@id='tabScenario']//table[@id='scenariotable']//tbody/tr/td[3 and text() = 'Testb']/..//input"));
		click(By.xpath("//div[@id='tabScenario']//a[contains(@onclick,'showEstimation')]"));
		testEditablePage(true, "section_scenario_assessment");
	}

	private void testAsset() throws InterruptedException, Exception {
		chooseElementInsideDropdown("//a[@href='#tabAsset']", false);
		// add asset

		addAsset("Testa", "HW", 1, true, "This is a test", "this test commentis hidden");
		addAsset("Testb", "SW", 2, false, "This is a test", "this test comment is hidden");
		addAsset("Testd", "HW", 3, true, "This is a test", "this test comment is hidden");

		// check selection
		selectionCheck("tabAsset", "selectAsset", false, "Testa", false, "Testb", false);
		selectionCheck("tabAsset", "selectAsset", false, "Testa", false, "Testb", true);
		selectionCheck("tabAsset", "selectAsset", false, "Testa", true, "Testb", false);

		selectionCheck("tabAsset", "selectAsset", true, "Testa", false, "Testb", false);
		selectionCheck("tabAsset", "selectAsset", true, "Testa", true, "Testb", true);

		// show estimation
		selectionCheck("tabAsset", "selectAsset", false, "Testa", true, "Testb", true);
		selectionCheck("tabAsset", "selectAsset", true, "Testa", true, "Testb", false);
		click(By.xpath("//a[contains(@onclick,'showEstimation')]"));
		testEditablePage(true, "tabEstimationAsset");

		click(By.xpath("//a[contains(@onclick,'switchTab') and contains(@onclick,'tabAsset')]"));

		selectionCheck("tabAsset", "selectAsset", true, "Testd", true, "Testa", false);
		click(By.xpath("//a[contains(@onclick,'showEstimation')]"));
		testEditablePage(true, "tabEstimationAsset");

		click(By.xpath("//a[contains(@onclick,'switchTab') and contains(@onclick,'tabAsset')]"));

		selectionCheck("tabAsset", "selectAsset", true, "Testd", false, "Testb", true);
		click(By.xpath("//a[contains(@onclick,'showEstimation')]"));
		testEditablePage(true, "tabEstimationAsset");

		click(By.xpath("//a[contains(@onclick,'switchTab') and contains(@onclick,'tabAsset')]"));
	}

	private void testVersion(String companyName, String analyseName, String newValue)
			throws InterruptedException, Exception {
		goToAllAnalysis(companyName, analyseName);
		// backup the id of the based analysis
		String oldId = findElement(By.xpath("//*[@id='section_analysis']/table/tbody/tr[1]"))
				.getAttribute("data-trick-id");

		// add version
		click(By.xpath("//a[contains(@onclick,'addHistory')]"));
		sendKeys(findElement(By.id("history_author")), "Deimos Chan");
		assert !sendKeys(findElement(By.id("history_oldVersion")), newValue);
		sendKeys(findElement(By.id("history_version")), "0.0.2");
		sendKeys(findElement(By.id("history_comment")), "Bla bla nium");
		click(By.id("history_submit_button"));
		new WebDriverWait(getDriver(), 20)
				.until(ExpectedConditions.invisibilityOfElementLocated(By.id("history_submit_button")));

		// check data
		selectCheckBox(true,
				By.xpath("//*[@id='section_analysis']/table/tbody/tr[not(@data-trick-id='" + oldId + "')]//input"));
		// read only
		testReadOnlyAnalysis(companyName, analyseName);
		// back to delete the new version
		goToAllAnalysis(companyName, analyseName);
		selectCheckBox(true,
				By.xpath("//*[@id='section_analysis']/table/tbody/tr[not(@data-trick-id='" + oldId + "')]//input"));
		click(By.xpath("//a[contains(@onclick,'deleteAnalysis')]"));
		click(By.id("deleteanalysisbuttonYes"));
		// back to analysis
		goToAllAnalysis(companyName, analyseName);
		selectCheckBox(true,
				By.xpath("//*[@id='section_analysis']/table/tbody/tr[@data-trick-id='" + oldId + "']//input"));
		click(By.xpath(
				"//li[contains(@data-trick-check,\"hasRight('MODIFY')\")]/a[contains(@onclick,'selectAnalysis')]"));
	}

	public void testReadOnlyAnalysis(String companyName, String analyseName) throws Exception {
		click(By.xpath(
				"//li[contains(@data-trick-check,\"hasRight('READ')\")]/a[contains(@onclick,'selectAnalysis')]"));
		testDefaultEditable(false);
	}

	public void testDefaultEditable(boolean editable) throws InterruptedException, Exception {

		if (editable) {
			testEditablePage(editable, "tabHistory");
		}

		chooseElementInsideDropdown("//a[@href='#tabScope']", editable);
		testEditablePage(editable, "tabScope");

		chooseElementInsideDropdown("//a[@href='#tabParameterImpactProba']", editable);
		testEditablePage(editable, "tabParameterImpactProba");

		chooseElementInsideDropdown("//a[@href='#tabParameterOther']", editable);
		testEditablePage(editable, "tabParameterOther");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Threat']", editable);
		testEditablePage(editable, "tabRiskInformation_Threat");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Vul']", editable);
		testEditablePage(editable, "tabRiskInformation_Vul");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Risk']", editable);
		testEditablePage(editable, "tabRiskInformation_Risk");
	}
}
