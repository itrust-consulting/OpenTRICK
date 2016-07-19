package lu.itrust.TS.ui.knowledgebase;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import lu.itrust.TS.ui.data.DataProviderSource;
import lu.itrust.TS.ui.tools.BaseUnitTesting;

public class Measures extends BaseUnitTesting {


	@Test(groups = { "addMeasure" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void addMeasure(String standardLabel,String reference, String level, Boolean computable, String domain,
			String measureDescription) throws InterruptedException {
		goToMeasures(standardLabel);
		click(By.xpath("//a[contains(@onclick,'newMeasure')]"));
		fillMesureInputFields(reference, level, computable, domain, measureDescription);
		click(By.id("addmeasurebutton"));
	}

	
	@Test(groups = { "addStandard" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void addStandard(String label, String version, String description, Boolean isComputable,
			String standardTypeValue) throws InterruptedException {
		goToStandard();
		click(By.xpath("//a[contains(@onclick,'newStandard()')]"));
		fillStandard(label, version, description, isComputable, standardTypeValue);
		click(By.id("addstandardbutton"));
	}

	
	@Test(groups = { "deleteMeasure" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void deleteMeasure(String standardLabel, String reference) throws InterruptedException {
		goToMeasures(standardLabel);
		selectMeasure(reference);
		click(By.xpath("//a[contains(@onclick,'deleteMeasure()')]"));
		click(By.xpath(
				"//div[@id='deleteMeasureModel'][@class='modal fade in']//button[@id='deletemeasurebuttonYes']"));
	}

	@Test(groups = { "deleteStandard" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void deleteStandard(String label) throws InterruptedException {
		goToStandard();
		selectStandard(label);

		click(By.xpath("//a[contains(@onclick,'deleteStandard()')]"));
		click(By.id("deletestandardbuttonYes"));
	}



	public void goToMeasures(String standardLabel) throws InterruptedException {
		goToStandard();
		selectStandard(standardLabel);
		click(By.xpath("//a[contains(@onclick,'tab_measure')]"));

		waitLoadingIndicator();
	}

	
	@Test(groups = { "updateMeasure" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void updateMeasure(String standardLabel, String updateThisReference, String reference, String level, Boolean computable,
			String domain, String measureDescription) throws InterruptedException {
		goToMeasures(standardLabel);
		selectMeasure(updateThisReference);
		click(By.xpath("//a[contains(@onclick,'editSingleMeasure')]"));
		fillMesureInputFields(reference, level, computable, domain, measureDescription);
		click(By.id("addmeasurebutton"));
	}

	
	@Test(groups = { "updateStandard" }, dataProvider = "dataProvider", dataProviderClass = DataProviderSource.class)
	public void updateStandard(String updateThisLabel, String label, String version, String description,
			Boolean isComputable, String standardTypeValue) throws InterruptedException {
		goToStandard();
		selectStandard(updateThisLabel);
		click(By.xpath("//a[contains(@onclick,'editSingleStandard()')]"));

		fillStandard(label, version, description, isComputable, standardTypeValue);
		click(By.id("addstandardbutton"));
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

	private void goToStandard() throws InterruptedException {
		goToKnowledgeBase();
		chooseElementInsideDropdown("//a[@href='#tab_standard']");
	}

	private void selectMeasure(String reference) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='tab_measure']//td[3][ text () = '" + reference + "']/..//input"));
	}

	private void selectStandard(String label) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='tab_standard']//td[ text() = '" + label + "'][1]/..//input"));
	}
}
