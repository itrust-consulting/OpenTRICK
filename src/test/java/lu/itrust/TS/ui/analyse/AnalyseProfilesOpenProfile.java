package lu.itrust.TS.ui.analyse;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class AnalyseProfilesOpenProfile extends BaseAnalyse {

	@Test(groups = { "analyseProfile" })
	public void analyseProfilesOpenProfile() throws Exception {

		goToKnowledgeBase();
		click(By.xpath("//a[@href='#tab_analyses']"));

		selectCheckBox(true, By.xpath("//div[@id='section_profile_analysis']//tbody/tr[1]//input"));

		click(By.xpath("//a[contains(@onclick,'selectAnalysis')]"));

		chooseElementInsideDropdown("//a[@href='#tabParameterImpactProba']");
		testEditablePage(true, "tabParameterImpactProba");

		chooseElementInsideDropdown("//a[@href='#tabParameterOther']");
		testEditablePage(true, "tabParameterOther");

		// Risk analysis - Scenarios
		chooseElementInsideDropdown("//a[@href='#tabScenario']");

		// add
		addScenario("Testb", "Confidentiality", "This is a test profile", true, new String[] { "Busi", "Compl" }, 0.25,
				0.25, 0.25, 0.25, 1.0, 1.0, 1.0, 1.0, 1.0);

		// Risk treatment / Compliance

		chooseElementInsideDropdown("//a[contains(@onclick,'manageStandard')]");

		click(By.xpath("//a[@href='#standard_form_container']"));

		fillStandard("Test", "This is a test.", true, "ASSET");
		click(By.name("save"));

		click(By.xpath("//a[@href='#standard_form_container']"));

		fillStandard("Testa", "This is a test.", true, "NORMAL");
		click(By.name("save"));

		int idStandardAssetMeasure = Integer
				.valueOf(findElement(By.xpath("//div[@id='standardModal']//tbody//tr/td[2 and text() ='Test']/.."))
						.getAttribute("data-trick-id"));
		int idStandardNormalMeasure = Integer
				.valueOf(findElement(By.xpath("//div[@id='standardModal']//tbody//tr/td[2 and text() ='Testa']/.."))
						.getAttribute("data-trick-id"));

		click(By.xpath("//div[@id='standardModal']//button[@data-dismiss='modal']"));

		chooseElementInsideDropdown("//a[@href='#tabStandard_" + idStandardNormalMeasure + "']");

		// testEditablePage(true, "tabStandard_" + idStandardNormalMeasure);

		// normal measure
		chooseElementInsideDropdown("//a[@href='#tabStandard_" + idStandardAssetMeasure + "']");
		testEditablePage(true, "tabStandard_" + idStandardAssetMeasure);

		// chooseElementInsideDropdown("//a[@href='#tabStandard_" + 1 + "']");
		// testEditablePage(true, "tabStandard_" + 1);

		// asset measure

		// later
		// Phases
		click(By.linkText("Risk treatment / Compliance"));
		click(By.xpath("//a[@href='#tabPhase']"));

		// Add

		// Edit
		// Delete
	}

}
