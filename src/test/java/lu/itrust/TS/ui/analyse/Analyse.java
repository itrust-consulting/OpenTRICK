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
	public void addAnalysis(String company, String language, String profile, String author, String version, String name, String description, boolean isUncertainty, boolean isCSSF)
			throws InterruptedException {
		super.addAnalysis(company, language, profile, author, version, name, description, isUncertainty, isCSSF);
	}

	@Test(groups = { "deleteAnalysis" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	@Override
	public void deleteAnalysis(String companyName, String analyseName) throws InterruptedException {
		super.deleteAnalysis(companyName, analyseName);
	}

	@Test(groups = { "editAnalysis" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void editAnalysis(String defaultCompanyName, String defaultAnalyseName, String newAnalyseName, String newCompanyName, String language, boolean isUncertainty,
			boolean isCSSF) throws InterruptedException {
		goToAllAnalysis(defaultCompanyName, defaultAnalyseName);
		click(By.xpath("//a[contains(@onclick,'editSingleAnalysis')]"));

		assert !sendKeys(findElement(By.id("analysis_identifier")), "");
		assert !sendKeys(findElement(By.id("analysis_version")), "");
		assert !sendKeys(findElement(By.id("analysis_creationDate")), "");
		assert !sendKeys(findElement(By.id("analysis_owner")), "");
		assert !findElement(By.id("analysis_hasData")).isEnabled();

		new Select(findElement(By.xpath("//div[@id='analysiscustomercontainer']//select"))).selectByVisibleText(newCompanyName);
		new Select(findElement(By.xpath("//div[@id='analysislanguagecontainer']//select"))).selectByVisibleText(language);

		sendKeys(findElement(By.xpath("//input[contains(@name,'comment')]")), newAnalyseName);

		selectCheckBox(isUncertainty, By.name("uncertainty"));
		selectCheckBox(isCSSF, By.name("cssf"));

		click(By.xpath("//div[@id='editAnalysisModel']//button[@id='editAnalysisButton']"));

		goToAllAnalysis(newCompanyName, newAnalyseName);
	}

	@Test(groups = { "analyse" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void testAnalyse(String companyName, String analyseName) throws Exception {
		String oldValue = "";
		String newValue = "";

		goToAllAnalysis(companyName, analyseName);

		// edit
		click(By.xpath("//li[contains(@data-trick-check,\"hasRight('MODIFY')\")]/a[contains(@onclick,'selectAnalysis')]"));

		testEditablePage(true, "tabHistory");

		chooseElementInsideDropdown("//a[@href='#tabScope']", false);

		testEditablePage(true, "tabScope");

		chooseElementInsideDropdown("//a[@href='#tabParameterImpactProba']", false);

		testEditablePage(true, "tabParameterImpactProba");

		chooseElementInsideDropdown("//a[@href='#tabParameterOther']", false);

		testEditablePage(true, "tabParameterOther");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Threat']", false);

		testEditablePage(true, "tabRiskInformation_Threat");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Vul']", false);

		testEditablePage(true, "tabRiskInformation_Vul");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Risk']", false);
		testEditablePage(true, "tabRiskInformation_Risk");

		// -----------------------------------------------------------------------------------
		// version
		goToAllAnalysis(companyName, analyseName);
		String oldId = findElement(By.xpath("//*[@id='section_analysis']/table/tbody/tr[1]")).getAttribute("data-trick-id");

		click(By.xpath("//a[contains(@onclick,'addHistory')]"));
		sendKeys(findElement(By.id("history_author")), "Deimos Chan");
		assert !sendKeys(findElement(By.id("history_oldVersion")), newValue);
		sendKeys(findElement(By.id("history_version")), "0.0.2");
		sendKeys(findElement(By.id("history_comment")), "Bla bla nium");
		click(By.id("history_submit_button"));
		new WebDriverWait(getDriver(), 20).until(ExpectedConditions.invisibilityOfElementLocated(By.id("history_submit_button")));

		selectCheckBox(true, By.xpath("//*[@id='section_analysis']/table/tbody/tr[not(@data-trick-id='" + oldId + "')]//input"));
		// read only
		testReadOnlyAnalysis(companyName, analyseName);
		// back to analysis
		// ------------------------------------------------------------------
		goToAllAnalysis(companyName, analyseName);
		click(By.xpath("//li[contains(@data-trick-check,\"hasRight('MODIFY')\")]/a[contains(@onclick,'selectAnalysis')]"));

		chooseElementInsideDropdown("//a[@href='#tabAsset']", false);
		// add asset
		// TODO add/edit/delete asset

		addAsset("Testa", "HW", 0, true, "This is a test", "this test commentis hidden");
		addAsset("Testb", "SW", 0, false, "This is a test", "this test comment is hidden");
		addAsset("Testd", "HW", 0, true, "This is a test", "this test comment	 is hidden");

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

		click(By.xpath("//a[contains(@onclick,'switchTab') and	 contains(@onclick,'tabAsset')]"));

		// scenario
		chooseElementInsideDropdown("//a[@href='#tabScenario']", false);

		// TODO add/edit/delete scenario, already done analyseprofileopenprofile

		addScenario("Test", "Confidentiality", "This is a test profile", true, new String[] { "SW", "HW" }, 0.25, 0.25, 0.25, 0.25, 1.0, 1.0, 1.0, 1.0, 1.0);
		addScenario("Testa", "Confidentiality", "This is a test profile", true, new String[] { "SW" }, 0.15, 0.25, 0.3, 0.3, 1.0, 0.1, 1.0, 0.1, 1.0);
		addScenario("Testb", "Confidentiality", "This is a test profile", true, new String[] { "HW" }, 0.15, 0.25, 0.3, 0.3, 1.0, 0.1, 1.0, 0.1, 1.0);

		// estimation
		chooseElementInsideDropdown("//a[@href='?open=edit-estimation']", false);
		click(By.xpath("//div[@role='left-menu']//div[@class='list-group']//a[contains(@class,'active')]/following-sibling::a[1]"));

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
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].focus();arguments[0].blur(); return true", findElement(By.name("impactFin")));

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
		chooseElementInsideDropdown("//a[contains(@onclick,'manageStandard')]", false);
		// get all standard id
		ArrayList<String> standardids = new ArrayList<>();
		findElements(By.xpath("//div[@id='standardModal']//tbody/tr")).forEach((element) -> {
			standardids.add(element.getAttribute("data-trick-id"));
		});
		click(By.xpath("//div[@id='standardModal']//button[@name='cancel']"));

		// standard fill
		for (int i = 0; i < standardids.size(); i++) {
			String standardId = "tabStandard_" + standardids.get(i);
			chooseElementInsideDropdown("//a[@href='#" + standardId + "']", false);
			testEditablePage(true, standardId);
		}

		// TODO Phases add/edit/delete

		// action plan compute
		chooseElementInsideDropdown("//a[@href='#tabActionPlan']", false);
		click(By.xpath("//ul[@id='menu_actionplans']//a[contains(@onclick,'displayActionPlanOptions')]"));
		click(By.xpath("//button[@id='computeActionPlanButton']"));
		new WebDriverWait(getDriver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//span[@id='task-counter' and text()='1']")));
		waitLoadingIndicator();

		assert findElements(By.xpath("//div[@id='tabActionPlan']//tbody/tr")).size() == findElements(By.xpath("//div[@id='tabActionPlan']//tbody/tr")).size()
				&& findElements(By.xpath("//div[@id='tabActionPlan']//tbody/tr")).size() == findElements(By.xpath("//div[@id='tabActionPlan']//tbody/tr")).size();

		//
		chooseElementInsideDropdown("//a[@href='#tabSOA']", false);
		testEditablePage(true, "tabSOA");
		chooseElementInsideDropdown("//a[@href='#tabSummary']", false);

		assert findElements(By.xpath("//div[@id='tabSummary']//tbody/tr")).size() == findElements(By.xpath("//div[@id='tabSummary']//tbody/tr")).size()
				&& findElements(By.xpath("//div[@id='tabSummary']//tbody/tr")).size() == findElements(By.xpath("//div[@id='tabSummary']//tbody/tr")).size();
		chooseElementInsideDropdown("//a[contains(@onclick,'return loadRRF();')]", false);
	}

	public void testReadOnlyAnalysis(String companyName, String analyseName) throws Exception {

		click(By.xpath("//li[contains(@data-trick-check,\"hasRight('READ')\")]/a[contains(@onclick,'selectAnalysis')]"));
		// testEditablePage(false, "tabHistory");

		chooseElementInsideDropdown("//a[@href='#tabScope']", false);

		testEditablePage(false, "tabScope");

		chooseElementInsideDropdown("//a[@href='#tabParameterImpactProba']", false);

		testEditablePage(false, "tabParameterImpactProba");

		chooseElementInsideDropdown("//a[@href='#tabParameterOther']", false);

		testEditablePage(false, "tabParameterOther");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Threat']", false);

		testEditablePage(false, "tabRiskInformation_Threat");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Vul']", false);

		testEditablePage(false, "tabRiskInformation_Vul");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Risk']", false);
		testEditablePage(false, "tabRiskInformation_Risk");
	}
}
