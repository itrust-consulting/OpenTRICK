package lu.itrust.TS.ui.analyse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.xpath.XPathExpressionException;

import org.dom4j.Document;
import org.dom4j.Text;
import org.dom4j.io.SAXReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import bsh.Interpreter;
import lu.itrust.TS.ui.tools.BaseUnitTesting;
import lu.itrust.TS.ui.tools.NeverStaleWebElement;

public class BaseAnalyse extends BaseUnitTesting {

	private static Document getDocument(String filename) {
		try {
			return new SAXReader().read(new File(filename));
		} catch (Exception e) {
			System.out.println("Error reading configuration file:");
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * @param name
	 * @param type
	 * @param description
	 * @param preventive
	 * @param detective
	 * @param limitative
	 * @param reactive
	 * @param intentional
	 * @param accidental
	 * @param internalThreat
	 * @param externalThreat
	 * @throws InterruptedException
	 */
	public void fillScenario(String name, String type, String description, Double preventive, Double detective,
			Double limitative, Double reactive, Double intentional, Double accidental, Double internalThreat,
			Double externalThreat) throws InterruptedException {
		assert canBeClicked(findElement(By.id("scenario_name")));
		sendKeys(findElement(By.id("scenario_name")), name);

		new Select(findElement(By.id("scenario_scenariotype_id"))).selectByVisibleText(type);

		sendKeys(findElement(By.id("scenario_description")), description);

		click(By.id("scenario_selected"));
		click(By.xpath("//a[@href='#tab_scenario_properties']"));
		// Identify WebElement
		moveSlider("scenario_preventive", preventive);
		moveSlider("scenario_detective", detective);
		moveSlider("scenario_limitative", limitative);
		moveSlider("scenario_corrective", reactive);

		moveSlider("scenario_intentional", intentional);
		moveSlider("scenario_accidental", accidental);
		moveSlider("scenario_internalThreat", internalThreat);
		moveSlider("scenario_externalThreat", externalThreat);
	}

	private boolean checkSelection(boolean stateCheckBox, boolean shouldSelect, boolean beforeState,
			boolean afterState) {
		return (stateCheckBox && shouldSelect && !afterState) || (stateCheckBox && !shouldSelect && afterState)
				|| (!stateCheckBox && shouldSelect && beforeState == afterState)
				|| (!stateCheckBox && !shouldSelect && beforeState == afterState);
	}

	private String dataFromRandom(NeverStaleWebElement fieldParameter, Interpreter interpreter,
			ArrayList<String> acronyms, NeverStaleWebElement input) {
		String oldvalue = input.getAttribute("value"), newValue = oldvalue;
		if (input.getTagName().equals("select")) {
			do {
				newValue = new Select(input).getOptions()
						.get(new Random().nextInt(new Select(input).getOptions().size())).getAttribute("value");
			} while (fieldParameter.getAttribute("data-trick-field-type").equals("string")
					&& new Select(input).getOptions().size() > 1 && (newValue.isEmpty() || newValue.equals(oldvalue)));

		} else if (fieldParameter.getAttribute("data-trick-field-type").equals("double")) {
			// TODO minvalue=1, wait until have attribute data-trick-min-value
			int minValue, maxValue;

			try {
				if (fieldParameter.getAttribute("data-trick-min-value") != null) {
					interpreter.eval("result = " + fieldParameter.getAttribute("data-trick-min-value"));
					minValue = (int) interpreter.get("result");
					if (fieldParameter.getAttribute("data-trick-max-value") != null) {
						interpreter.eval("result = " + fieldParameter.getAttribute("data-trick-max-value"));
						maxValue = (int) interpreter.get("result");
					} else {
						maxValue = Integer.MAX_VALUE - 1;
					}
				} else {
					interpreter.eval("result = "
							+ fieldParameter.findElement(By.xpath("..")).getAttribute("data-trick-min-value"));
					minValue = (int) interpreter.get("result");
					if (fieldParameter.getAttribute("data-trick-max-value") != null) {
						interpreter.eval("result = " + fieldParameter.getAttribute("data-trick-max-value"));
						maxValue = (int) interpreter.get("result");
					} else {
						maxValue = Integer.MAX_VALUE - 1;
					}
				}
				assert maxValue >= minValue;
			} catch (Exception e1) {
				e1.printStackTrace();
				minValue = 2;
				maxValue = 20;
			}

			do {
				newValue = new Random().nextInt(maxValue - minValue + 1) + minValue + "";
			} while (newValue.equals(oldvalue) && (Math.abs(maxValue - minValue) > 1));

		} else if (fieldParameter.getAttribute("data-trick-field-type").equals("string")) {

			if (fieldParameter.getAttribute("data-trick-field").equals("impactFin")) {
				newValue = acronyms.get(new Random().nextInt(acronyms.size()));
			} else if (oldvalue.equals("Test")) {
				newValue = "Testa";
			} else {
				newValue = "Test";
			}
		} else {
			throw new RuntimeException("Data type unknown");

		}
		return newValue;
	}

	private void fillAnalyse(String company, String language, String profile, String description, String name,
			boolean isUncertainty, boolean isCSSF) throws InterruptedException {
		if (company == null) {
			selectComboboxByIndex(By.name("customer"), 1);
		} else
			new Select(findElement(By.name("customer"))).selectByVisibleText(company);

		if (language == null)
			selectComboboxByIndex(By.name("language"), 1);
		else
			new Select(findElement(By.name("language"))).selectByVisibleText(language);

		if (profile == null)
			selectComboboxByIndex(By.id("analysis_profile"), 1);
		else
			new Select(findElement(By.id("analysis_profile"))).selectByVisibleText(profile);

		sendKeys(findElement(By.name("name")), name);

		sendKeys(findElement(By.xpath("//div[@id='buildAnalysisModal']//textarea")), description);

		selectCheckBox(isUncertainty, By.name("uncertainty"));
		selectCheckBox(isCSSF, By.id("cssf"));
	}

	private void fillAsset(String name, String type, int value, Boolean isSelected, String comment,
			String hiddenComment) throws InterruptedException {

		sendKeys(findElement(By.xpath("//div[@id='addAssetModal']//input[@id='asset_name']")), name);

		new Select(findElement(By.xpath("//div[@id='addAssetModal']//select[@id='asset_assettype_id']")))
				.selectByVisibleText(type);
		;
		sendKeys(findElement(By.xpath("//div[@id='addAssetModal']//input[@id='asset_value']")), value + "");

		selectCheckBox(isSelected, By.xpath("//div[@id='addAssetModal']//input[@id='asset_selected']"));

		sendKeys(findElement(By.xpath("//div[@id='addAssetModal']//textarea[@id='asset_comment']")), comment);
		sendKeys(findElement(By.xpath("//div[@id='addAssetModal']//textarea[@id='asset_hiddenComment']")),
				hiddenComment);

	}

	@SuppressWarnings("unchecked")
	private List<String> getData(String section) throws XPathExpressionException {
		Document document = getDocument(System.getProperty("user.dir") + "/src/test/resources/data/input-test.xml");
		return (List<String>) document.selectNodes("//tab[@id='" + section + "']//value/text()").stream().map(node-> ((Text)node).getText()).collect(Collectors.toList());
	}

	// tools
	private void moveSlider(String idSlider, double percentage) {
		WebElement slider = getDriver()
				.findElement(By.xpath("//input[@id='" + idSlider + "']/../div[@class='slider slider-vertical']"));

		WebElement bullet = slider.findElement(By.className("slider-handle"));

		Actions move = new Actions(getDriver());
		move.dragAndDropBy(bullet, 0, (int) (-slider.getLocation().y + bullet.getLocation().y));
		move.dragAndDropBy(bullet, 0, (int) (-slider.getSize().getHeight() * percentage));
		move.perform();
	}

	private void sendData(String newValue, NeverStaleWebElement fieldParameter, NeverStaleWebElement input)
			throws XPathExpressionException, InterruptedException {
		if (input.getTagName().equals("select")) {
			selectComboboxByIndex(input.getFoundBy(), Integer.parseInt(
					input.findElement(By.xpath("//option[@value='" + newValue + "']")).getAttribute("index")));
		} else if ((fieldParameter.getAttribute("data-trick-field-type").equals("double"))
				|| (fieldParameter.getAttribute("data-trick-field-type").equals("string"))) {
			sendKeys(input.getElement(), newValue);
		} else {
			throw new RuntimeException("Data type unknown");
		}
	}

	protected void addAnalysis(String company, String language, String profile, String author, String version,
			String name, String description, boolean isUncertainty, boolean isCSSF) throws InterruptedException {
		System.out.println(company);
		goToAllAnalysis(company, null);
		click(By.xpath("//a[contains(@onclick,'customAnalysis(this)')]"));

		fillAnalyse(company, language, profile, description, name, isUncertainty, isCSSF);

		click(By.xpath("//div[@id='buildAnalysisModal']//button[@name='save']"));

		WebDriverWait wait = new WebDriverWait(getDriver(), 60);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='buildAnalysisModal']")));

		assert !isElementPresent(By.xpath("//div[@id='buildAnalysisModal']"));
	}

	// asset
	protected void addAsset(String name, String type, int value, Boolean isSelected, String comment,
			String hiddenComment) throws InterruptedException {
		click(By.xpath("//a[@onclick='return editAsset(undefined,true);']"));
		assert !findElement(By.xpath("//div[@id='addAssetModal']//input[@id='asset_selected']")).isSelected();

		fillAsset(name, type, value, isSelected, comment, hiddenComment);

		click(By.xpath("//div[@id='addAssetModal']//button[contains(@onclick,'saveAsset')]"));
	}

	// scenario
	protected void addScenario(String name, String type, String description, Boolean isSelected,
			String[] applicablesAssetTypes, Double preventive, Double detective, Double limitative, Double reactive,
			Double intentional, Double accidental, Double environmental, Double internalThreat, Double externalThreat)
			throws InterruptedException {
		click(By.xpath("//a[contains(@onclick,'editScenario(undefined,true)')]"));

		fillScenario(name, type, description, preventive, detective, limitative, reactive, intentional, accidental,
				internalThreat, externalThreat);

		click(By.xpath("//button[contains(@onclick,'saveScenario')]"));
	}

	protected void deleteAnalysis(String companyName, String analyseName) throws InterruptedException {
		goToAllAnalysis(companyName, analyseName);

		click(By.xpath("//a[contains(@onclick,'deleteAnalysis')]"));
		click(By.id("deleteanalysisbuttonYes"));

		WebDriverWait wait = new WebDriverWait(getDriver(), 30);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("//div[@id='section_analysis']//tbody//td[2 and string() = '" + analyseName + "']")));

		assert findElement(
				By.xpath("//div[@id='section_analysis']//tbody//td[2 and string() = '" + analyseName + "']")) == null;
	}

	// standard
	protected void fillStandard(String label, String description, Boolean isComputable, String standardTypeValue)
			throws InterruptedException {
		sendKeys(findElement(By.id("standard_label")), label);
		sendKeys(findElement(By.id("standard_description")), description);

		selectCheckBox(isComputable, By.id("standard_computable"));

		if (standardTypeValue != null)
			click(By.xpath("//input[@value='" + standardTypeValue + "']"));
	}

	protected void goToProfile() throws InterruptedException {
		goToKnowledgeBase();
		click(By.xpath("//a[@href='#tab_analyses']"));
	}

	protected void selectionCheck(String tab, String actionId, boolean shouldSelect, String textA, boolean stateA,
			String textB, Boolean stateB) throws InterruptedException {
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
				"//li[contains(@data-trick-single-check,'isSelected') or contains(@data-trick-check,'isSelected')]//a[contains(@onclick,'"
						+ actionId + "') and contains(@onclick,'" + (shouldSelect ? "true" : "false") + "')]"));
		// verify if state had changed successfully
		assert checkSelection(stateA, shouldSelect, beforeStateA,
				findElement(By.xpath("//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textA
						+ "']/../.[contains(@class,'warning') or contains(@class,'success')]")) == null)
				&& checkSelection(stateB, shouldSelect, beforeStateB,
						findElement(By.xpath("//div[@id='" + tab + "']//tbody//tr/td[2 and text() ='" + textB
								+ "']/../.[contains(@class,'warning') or contains(@class,'success')]")) == null);
	}

	
	protected synchronized void testEditablePage(Boolean shouldEdit, String section) throws Exception {
		chooseElementInsideDropdown("//a[contains(@onclick,'reloadCharts')]", true);
		Interpreter interpreter = new Interpreter();

		int currentValuesIndex = 0;
		List<String> values = getData(section);

		WebElement impactScale = findElement(By.xpath("//div[@id='" + section
				+ "']//a[contains(@onclick,'displayParameters') and contains(@onclick,'Scale_Impact')]"));
		ArrayList<String> acronyms = new ArrayList<>();
		if (impactScale != null) {
			WebDriverWait wait = new WebDriverWait(getDriver(), 30);
			wait.until(ExpectedConditions.visibilityOf(impactScale));
			click(By.xpath("//div[@id='" + section
					+ "']//a[contains(@onclick,'displayParameters') and contains(@onclick,'Scale_Impact')]"));
			waitClick(By.xpath("//div[@id='modalBox']//tr[@data-trick-class='ExtendedParameter']/td[2]"));
			for (WebElement acronymElement : getDriver()
					.findElements(By.xpath("//div[@id='modalBox']//tr[@data-trick-class='ExtendedParameter']/td[2]"))) {
				acronyms.add(acronymElement.getText());
			}
			click(By.xpath("//div[@id='modalBox']//*[@data-dismiss='modal']"));
		}

		// explorer les champs
		ArrayList<String> xpathCurrents = new ArrayList<>();

		List<WebElement> elements = getDriver()
				.findElements(By.xpath("//div[@id='" + section + "']//td[contains(@onclick,'editField')]/.."));
		int size = elements.size();
		System.out.println("//div[@id='" + section + "']//td[contains(@onclick,'editField')]/..");

		for (int index = 0; index < size; index++) {
			System.out.println("Xpath generate :  " + index + "/" + (size - 1));
			WebElement currentElement = elements.get(index);
			String xpathCurrent = "";

			int editFieldSize = currentElement.findElements(By.xpath("td[contains(@onclick,'editField')]")).size();

			if (currentElement.getAttribute("data-trick-id") != null) {
				// tr
				for (int j = 1; j <= editFieldSize; j++) {
					xpathCurrent = "(//div[@id='" + section + "']//tr[@data-trick-id='"
							+ currentElement.getAttribute("data-trick-id") + "']/td[contains(@onclick,'editField')])["
							+ j + "]";
					xpathCurrents.add(xpathCurrent);
				}

			} else {
				// td
				for (int j = 0; j < editFieldSize; j++) {
					xpathCurrent = "//div[@id='" + section
							+ "']//td[contains(@onclick,'editField') and @data-trick-id='"
							+ currentElement.findElements(By.cssSelector("td")).get(j).getAttribute("data-trick-id")
							+ "']";
					xpathCurrents.add(xpathCurrent);
				}
			}
		}
		for (int index = 1; index <= xpathCurrents.size(); index++) {
			String xpathCurrent = xpathCurrents.get(index - 1);
			NeverStaleWebElement possibleInput = new NeverStaleWebElement(getDriver(), By.xpath(xpathCurrent));

			// cliquer un champs
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			try {
				js.executeScript(
						"window.scrollTo(" + 0 + "," + (possibleInput.getElement().getLocation().y - 350) + ");");
			} catch (Exception e) {
				printError(e);
			}
			click(By.xpath(xpathCurrent));
			// verify

			WebElement activeElement = possibleInput
					.findElement(By.cssSelector("select.form-control,textarea.form-control,input.form-control"));
			if (activeElement != null) {
				String tagnameActiveElement = activeElement.getTagName();
				assert (shouldEdit == true && getInputPattern().matcher(tagnameActiveElement).matches()
						|| (shouldEdit == false && !getInputPattern().matcher(tagnameActiveElement).matches()));

				String xpathInput = "(" + xpathCurrent + ")//" + tagnameActiveElement
						+ "[not(@disabled)]";
				NeverStaleWebElement input = new NeverStaleWebElement(getDriver(), By.xpath(xpathInput));
				if (shouldEdit) {
					String newValue = "";
					NeverStaleWebElement fieldParameter = new NeverStaleWebElement(getDriver(),
							By.xpath(xpathInput + "//ancestor-or-self::*[@data-trick-field-type]"));

					try {
						newValue = values.get(currentValuesIndex).toString();
					} catch (Exception e) {
						printError(e);
						newValue = dataFromRandom(fieldParameter, interpreter, acronyms, input);
					}

					sendData(newValue, fieldParameter, input);
					// lost focus

					((JavascriptExecutor) getDriver()).executeScript(
							"arguments[0].focus(); arguments[0].blur(); return true", input.getElement());
					
					// TODO stalement
					new WebDriverWait(getDriver(), 10).until(ExpectedConditions.not(ExpectedConditions.visibilityOfNestedElementsLocatedBy(possibleInput.getElement(), By.className("form-control"))));
					
					assert possibleInput.getText().equals(newValue);

				} else {
					if (currentValuesIndex < values.size() && currentValuesIndex >= 0)
						assert values.get(currentValuesIndex).equals(possibleInput.getText());
				}
			} else {
				assert !shouldEdit;
				if (currentValuesIndex < values.size() && currentValuesIndex >= 0)
					assert values.get(currentValuesIndex).equals(possibleInput.getText());
			}
			currentValuesIndex++;
		}
	}
}
