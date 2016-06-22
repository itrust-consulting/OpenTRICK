package lu.itrust.TS.ui.knowledgebase;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.BaseUnitTesting;

public class Measures extends BaseUnitTesting {

	@Parameters(value = { "label", "version", "description", "isComputable", "standardTypeValue", "editLabel",
			"editVersion", "editDescription", "editIsComputable", "editStandardTypeValue" })
	@Test(groups = { "standardsMeasuresAddEditDelete" })
	public void testStandardsMeasuresAddEditDelete(String label, String version, String description,
			Boolean isComputable, String standardTypeValue,

			String editLabel, String editVersion, String editDescription, Boolean editIsComputable,
			String editStandardTypeValue) throws Exception {
		goToMeasures();
		// add
		addStandard(label, version, description, isComputable, standardTypeValue);
		// Edit
		updateStandard(label, editLabel, editVersion, editDescription, editIsComputable, editStandardTypeValue);
		// Delete
		deleteStandard(editLabel);
	}

	@Parameters(value = { "label", "version", "description", "isComputable", "standardTypeValue" })
	@Test(groups = { "addStandard" })
	public void addStandard(String label, String version, String description, Boolean isComputable,
			String standardTypeValue) throws InterruptedException {
		goToMeasures();
		click(By.xpath("//a[contains(@onclick,'newStandard()')]"));
		fillStandard(label, version, description, isComputable, standardTypeValue);
		click(By.id("addstandardbutton"));
	}

	@Parameters(value = { "label", "version", "description", "isComputable", "standardTypeValue" })
	@Test(groups = { "updateStandard" })
	public void updateStandard(String updateThisLabel, String label, String version, String description,
			Boolean isComputable, String standardTypeValue) throws InterruptedException {
		goToMeasures();
		selectStandard(updateThisLabel);
		click(By.xpath("//a[contains(@onclick,'editSingleStandard()')]"));

		fillStandard(label, version, description, isComputable, standardTypeValue);
		click(By.id("addstandardbutton"));
	}

	@Parameters(value = { "label" })
	@Test(groups = { "deleteStandard" })
	public void deleteStandard(String label) throws InterruptedException {
		goToMeasures();
		selectStandard(label);

		click(By.xpath("//a[contains(@onclick,'deleteStandard()')]"));
		click(By.id("deletestandardbuttonYes"));
	}

	private void selectStandard(String label) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='tab_standard']//td[ text() = '" + label + "'][1]/..//input"));
	}

	@Parameters(value = { "label", "reference", "level", "computable", "domain", "measureDescription", "editReference",
			"editLevel", "editComputable", "editDomain", "editMeasureDescription" })
	@Test(groups = { "testMeasures" })
	public void testMeasures(String standardLabel, String reference, String level, Boolean measureComputable,
			String domain, String measureDescription, String editReference, String editLevel,
			Boolean editMeasureComputable, String editDomain, String editMeasureDescription) throws Exception {
		// measures
		selectStandard(standardLabel);
		click(By.xpath("//a[contains(@onclick,'tab_measure')]"));

		WebDriverWait wait = new WebDriverWait(getDriver(), 30);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("#loading-indicator")));

		assert !isElementPresent(By.id("#loading-indicator"));

		// add a measure
		addMeasure(reference, level, measureComputable, domain, measureDescription);
		// edit
		updateMeasure(reference, editReference, editLevel, editMeasureComputable, editDomain, editMeasureDescription);
		// delete
		deleteMeasure(editReference);
		// Delete
		deleteStandard(standardLabel);
	}

	@Parameters(value = { "reference", "level", "level", "computable", "domain", "measureDescription" })
	@Test(groups = { "addMeasure" })
	public void addMeasure(String reference, String level, Boolean computable, String domain, String measureDescription)
			throws InterruptedException {
		click(By.xpath("//a[contains(@onclick,'newMeasure')]"));
		fillMesureInputFields(reference, level, computable, domain, measureDescription);
		click(By.id("addmeasurebutton"));
	}

	@Parameters(value = { "updateThisReference", "reference", "level", "level", "computable", "domain",
			"measureDescription" })
	@Test(groups = { "updateMeasure" })
	public void updateMeasure(String updateThisReference, String reference, String level, Boolean computable,
			String domain, String measureDescription) throws InterruptedException {
		selectMeasure(updateThisReference);
		click(By.xpath("//a[contains(@onclick,'editSingleMeasure')]"));
		fillMesureInputFields(reference, level, computable, domain, measureDescription);
		click(By.id("addmeasurebutton"));
	}

	@Parameters(value = { "reference" })
	@Test(groups = { "deleteMeasure" })
	public void deleteMeasure(String reference) throws InterruptedException {
		selectMeasure(reference);
		click(By.xpath("//a[contains(@onclick,'deleteMeasure()')]"));
		click(By.xpath(
				"//div[@id='deleteMeasureModel'][@class='modal fade in']//button[@id='deletemeasurebuttonYes']"));
	}

	private void selectMeasure(String reference) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='tab_measure']//td[3][ text () = '" + reference + "']/..//input"));
	}

	private void goToMeasures() throws InterruptedException {
		goToKnowledgeBase();
		chooseElementInsideDropdown("//a[@href='#tab_standard']");
	}

	private void fillMesureInputFields(String reference, String level, boolean isComputable, String domain,
			String description) throws InterruptedException {
		selectCheckBox(isComputable, By.id("measure_computable"));

		sendKeys(findElement(By.id("measure_reference")), reference);
		sendKeys(findElement(By.id("measure_level")), level);
		sendKeys(findElement(By.id("measure_domain_1")), domain);
		sendKeys(findElement(By.id("measure_description_1")), description);

	}

	private void fillStandard(String label, String version, String description, Boolean isComputable,
			String standardTypeValue) throws InterruptedException {
		sendKeys(findElement(By.id("standard_label")), label);
		sendKeys(findElement(By.id("standard_version")), version);
		sendKeys(findElement(By.id("standard_description")), description);

		selectCheckBox(isComputable, By.id("standard_computable"));

		click(By.xpath("//div[@class='panel-body']//input[@value='" + standardTypeValue + "']"));
	}
}
