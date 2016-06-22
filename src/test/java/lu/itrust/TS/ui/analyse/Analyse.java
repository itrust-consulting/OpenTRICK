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

public class Analyse extends BaseAnalyse {

	@Test(groups = { "analyse" })
	public void testAnalyse() throws Exception {

		String oldValue = "";
		String newValue = "";

		click(By.xpath("//a[substring-before(@href,'/Analysis')]"));
		// check if already in a analysis/profile to quit
		if (isElementPresent(By.xpath("//a[contains(@href,'/Analysis/Deselect')]"))) {
			click(By.xpath("//a[contains(@href,'/Analysis/Deselect')]"));
			click(By.xpath("//a[substring-before(@href,'/Analysis')]"));
		}

		click(By.xpath("//a[contains(@onclick,'customAnalysis(this)')]"));

		new Select(findElement(By.name("customer"))).selectByIndex(1);
		new Select(findElement(By.name("language"))).selectByIndex(1);

		new Select(findElement(By.id("analysis_profile"))).selectByIndex(1);

		sendKeys(findElement(By.name("name")), "TestAnalyse");

		Thread.sleep(600);
		sendKeys(findElement(By.xpath("//div[@id='buildAnalysisModal']//textarea")), "TestAnalyse");
		click(By.xpath("//div[@id='buildAnalysisModal']//button[@name='save']"));

		WebDriverWait wait = new WebDriverWait(getDriver(), 60);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='buildAnalysisModal']")));

		assert !isElementPresent(By.xpath("//div[@id='buildAnalysisModal']"));

		click(By.xpath("//div[@id='section_analysis']//tbody//td[2 and string() = 'TestAnalyse']"));

		// edit
		click(By.xpath(
				"//li[contains(@data-trick-check,\"hasRight('MODIFY')\")]/a[contains(@onclick,'selectAnalysis')]"));

		testEditablePage(true, "tabHistory");

		chooseElementInsideDropdown("//a[@href='#tabScope']");

		testEditablePage(true, "tabScope");

		chooseElementInsideDropdown("//a[@href='#tabParameterImpactProba']");

		testEditablePage(true, "tabParameterImpactProba");

		chooseElementInsideDropdown("//a[@href='#tabParameterOther']");

		testEditablePage(true, "tabParameterOther");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Threat']");

		testEditablePage(true, "tabRiskInformation_Threat");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Vul']");

		testEditablePage(true, "tabRiskInformation_Vul");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Risk']");
		testEditablePage(true, "tabRiskInformation_Risk");

		chooseElementInsideDropdown("//a[@href='#tabAsset']");

		// add asset
		addAsset("Testa", "HW", 0, true, "This is a test", "this test comment is hidden");
		addAsset("Testb", "SW", 0, false, "This is a test", "this test comment is hidden");

		selectionCheck("tabAsset", false, "Testa", false, "Testb", false);
		selectionCheck("tabAsset", false, "Testa", false, "Testb", true);
		selectionCheck("tabAsset", false, "Testa", true, "Testb", false);
		selectionCheck("tabAsset", false, "Testa", true, "Testb", true);

		selectionCheck("tabAsset", true, "Testa", false, "Testb", false);
		selectionCheck("tabAsset", true, "Testa", false, "Testb", true);
		selectionCheck("tabAsset", true, "Testa", true, "Testb", false);
		selectionCheck("tabAsset", true, "Testa", true, "Testb", true);

		// show estimation
		selectionCheck("tabAsset", false, "Testa", true, "Testb", true);
		selectionCheck("tabAsset", true, "Testa", false, "Testb", true);
		click(By.xpath("//a[contains(@onclick,'showEstimation')]"));
		testEditablePage(true, "tabEstimationAsset");

		click(By.xpath("//a[contains(@onclick,'switchTab') and contains(@onclick,'tabAsset')]"));
		// scenario
		chooseElementInsideDropdown("//a[@href='#tabScenario']");

		// add scenario
		addScenario("Test", "Confidentiality", "This is a test profile", true, new String[] { "Busi", "HW" }, 0.25,
				0.25, 0.25, 0.25, 1.0, 1.0, 1.0, 1.0, 1.0);
		addScenario("Testa", "Confidentiality", "This is a test profile", true, new String[] { "Busi", "SW" }, 0.15,
				0.25, 0.3, 0.3, 1.0, 0.1, 1.0, 0.1, 1.0);

		chooseElementInsideDropdown("//a[@href='?open=edit-estimation']");
		click(By.xpath("//div[@role='left-menu']//div[@class='list-group']//a[@data-trick-type]"));
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
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].focus(); arguments[0].blur(); return true",
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

		// todo estimation

		// read only
		Thread.sleep(600);

		click(By.xpath("//a[contains(@href,'/Analysis/All')]"));
		click(By.xpath("//a[substring-before(@href,'/Analysis')]"));
		click(By.xpath("//div[@id='section_analysis']//tbody//td[2 and string() = 'TestAnalyse']"));
		click(By.xpath(
				"//li[contains(@data-trick-check,\"hasRight('READ')\")]/a[contains(@onclick,'selectAnalysis')]"));
		testEditablePage(false, "tabHistory");

		chooseElementInsideDropdown("//a[@href='#tabScope']");

		testEditablePage(false, "tabScope");

		chooseElementInsideDropdown("//a[@href='#tabParameterImpactProba']");

		testEditablePage(false, "tabParameterImpactProba");

		chooseElementInsideDropdown("//a[@href='#tabParameterOther']");

		testEditablePage(false, "tabParameterOther");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Threat']");

		testEditablePage(false, "tabRiskInformation_Threat");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Vul']");

		testEditablePage(false, "tabRiskInformation_Vul");

		chooseElementInsideDropdown("//a[@href='#tabRiskInformation_Risk']");
		testEditablePage(false, "tabRiskInformation_Risk");

	}

	protected void selectionCheck(String tab, boolean shouldSelect, String textA, boolean stateA, String textB,
			Boolean stateB) throws InterruptedException {
		// checkbox a
		selectCheckBox(stateA, By.xpath(
				"//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textA + "']/..//input[@type='checkbox']"));

		// checkbox b
		selectCheckBox(stateB, By.xpath(
				"//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textB + "']/..//input[@type='checkbox']"));

		// save states from element a and b
		boolean beforeStateA = findElement(By.xpath("//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textA
				+ "']/../.[contains(@class,'warning') or contains(@class,'success')]")) == null;
		boolean beforeStateB = findElement(By.xpath("//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textB
				+ "']/../.[contains(@class,'warning') or contains(@class,'success')]")) == null;

		// click select or unselect depends on shouldSelect variable
		click(By.xpath(
				"//li[contains(@data-trick-single-check,'isSelected')]//a[contains(@onclick,'selectAsset') and contains(@onclick,'"
						+ (shouldSelect ? "true" : "false") + "')]"));
		// verify if state had changed successfully
		assert checkSelection(stateA, shouldSelect, beforeStateA,
				findElement(By.xpath("//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textA
						+ "']/../.[contains(@class,'warning') or contains(@class,'success')]")) == null)
				&& checkSelection(stateB, shouldSelect, beforeStateB,
						findElement(By.xpath("//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textB
								+ "']/../.[contains(@class,'warning') or contains(@class,'success')]")) == null);

	}

	private boolean checkSelection(boolean stateCheckBox, boolean shouldSelect, boolean beforeState,
			boolean afterState) {
		return (stateCheckBox && shouldSelect && !afterState) || (stateCheckBox && !shouldSelect && afterState)
				|| (!stateCheckBox && shouldSelect && beforeState == afterState)
				|| (!stateCheckBox && !shouldSelect && beforeState == afterState);
	}

}
