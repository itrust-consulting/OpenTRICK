package lu.itrust.TS.ui.analyse;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import bsh.Interpreter;
import lu.itrust.TS.ui.BaseUnitTesting;
import lu.itrust.TS.ui.NeverStaleWebElement;

public class BaseAnalyse extends BaseUnitTesting {
	protected synchronized void testEditablePage(Boolean shouldEdit, String section)
			throws Exception {
		try {
			Interpreter interpreter = new Interpreter();
			// impact scale check
			WebElement impactScale = findElement(By.xpath("//div[@id='" + section
					+ "']//a[contains(@onclick,'displayParameters') and contains(@onclick,'Scale_Impact')]"));
			ArrayList<String> acronyms = new ArrayList<>();
			if (impactScale != null) {
				WebDriverWait wait = new WebDriverWait(getDriver(), 30);
				wait.until(ExpectedConditions.visibilityOf(impactScale));
				click(By.xpath("//div[@id='" + section
						+ "']//a[contains(@onclick,'displayParameters') and contains(@onclick,'Scale_Impact')]"));
				waitClick(By.xpath("//div[@id='modalBox']//tr[@data-trick-class='ExtendedParameter']/td[2]"));
				for (WebElement acronymElement : getDriver().findElements(
						By.xpath("//div[@id='modalBox']//tr[@data-trick-class='ExtendedParameter']/td[2]"))) {
					acronyms.add(acronymElement.getText());
				}
				click(By.xpath("//div[@id='modalBox']//*[@data-dismiss='modal']"));
			}

			((JavascriptExecutor) getDriver())
					.executeScript("$('.navbar-fixed-top,.navbar-fixed-bottom,.nav-tabs,.affix,.affix-top').hide()");
			((JavascriptExecutor) getDriver())
					.executeScript("$(\"#" + section + " table\").stickyTableHeaders('destroy');");

			// explorer les champs
			ArrayList<String> xpathCurrents = new ArrayList<>();

			for (int index = 1; index <= getDriver()
					.findElements(By.xpath("//div[@id='" + section + "']//td[contains(@onclick,'editField')]"))
					.size(); index++) {
				WebElement currentElement = findElement(By.xpath(
						("((//div[@id='" + section + "']//td[contains(@onclick,'editField')])[" + index + "])")));

				String xpathCurrent = "";
				if (currentElement.getAttribute("data-trick-id") == null) {
					// tr
					String currentId = currentElement.findElement(By.xpath("..")).getAttribute("data-trick-id");
					int currentIndexRow = getDriver()
							.findElements(
									By.xpath("((//div[@id='" + section + "']//td[contains(@onclick,'editField')])["
											+ index + "])/preceding-sibling::td[contains(@onclick,'editField')]"))
							.size() + 1;
					xpathCurrent = "(//div[@id='" + section + "']//tr[@data-trick-id='" + currentId
							+ "']/td[contains(@onclick,'editField')])[" + currentIndexRow + "]";
				} else {
					// td
					xpathCurrent = "//div[@id='" + section
							+ "']//td[contains(@onclick,'editField') and @data-trick-id='"
							+ currentElement.getAttribute("data-trick-id") + "']";
				}
				xpathCurrents.add(xpathCurrent);
			}
			for (int index = 1; index <= xpathCurrents.size(); index++) {
				String xpathCurrent = xpathCurrents.get(index - 1);
				NeverStaleWebElement possibleInput = new NeverStaleWebElement(getDriver(), By.xpath(xpathCurrent));

				// cliquer un champs

				possibleInput.click();

				// verify

				WebElement activeElement = possibleInput.findElement(By.cssSelector("select,textarea,input"));
				if (activeElement != null) {
					String tagnameActiveElement = activeElement.getTagName();
					assert (shouldEdit == true && getInputPattern().matcher(tagnameActiveElement).matches()
							|| (shouldEdit == false && !getInputPattern().matcher(tagnameActiveElement).matches()));

					String xpathInput = "(" + xpathCurrent + ")//" + tagnameActiveElement + "[not(@disabled)]";
					NeverStaleWebElement input = new NeverStaleWebElement(getDriver(), By.xpath(xpathInput));
					if (shouldEdit) {
						String newValue = "";
						try {
							newValue = dataFromXML(section, index, possibleInput, xpathInput, input);
						} catch (Exception e) {
							newValue = dataFromRandom(interpreter, acronyms, xpathInput, input);
						}

						// lost focus
						((JavascriptExecutor) getDriver()).executeScript(
								"arguments[0].focus(); arguments[0].blur(); return true", input.getElement());

						Thread.sleep(600);
						assert possibleInput.getText().equals(newValue);
					}
				} else {
					assert !shouldEdit;
				}
			}
		} finally {
			((JavascriptExecutor) getDriver())
					.executeScript("$('.navbar-fixed-top,.navbar-fixed-bottom,.nav-tabs,.affix,.affix-top').show()");
		}

	}

	private String dataFromRandom(Interpreter interpreter, ArrayList<String> acronyms, String xpathInput,
			NeverStaleWebElement input) {
		NeverStaleWebElement parentInput = new NeverStaleWebElement(getDriver(), By.xpath(xpathInput + "/.."));

		if (parentInput.getTagName().equals("span")) {
			parentInput = new NeverStaleWebElement(getDriver(),
					By.xpath(xpathInput + "//ancestor::*[@data-trick-field-type]"));
		}

		String oldvalue = input.getAttribute("value"), newValue = oldvalue;
		if (input.getTagName().equals("select")) {
			do {
				newValue = new Select(input).getOptions()
						.get(new Random().nextInt(new Select(input).getOptions().size())).getAttribute("value");

				new Select(input).selectByValue(newValue);
			} while (parentInput.getAttribute("data-trick-field-type").equals("string")
					&& new Select(input).getOptions().size() > 1
					&& ((new Select(input).getFirstSelectedOption().getText().isEmpty()) || newValue.equals(oldvalue)));

		} else if (parentInput.getAttribute("data-trick-field-type").equals("double")) {
			int minValue = 1, maxValue = 20;

			try {
				if (parentInput.getAttribute("data-trick-min-value") != null) {
					interpreter.eval("result = " + parentInput.getAttribute("data-trick-min-value"));
					minValue = (int) interpreter.get("result");
					if (parentInput.getAttribute("data-trick-max-value") != null) {
						interpreter.eval("result = " + parentInput.getAttribute("data-trick-max-value"));
						maxValue = (int) interpreter.get("result");
					} else {
						maxValue = Integer.MAX_VALUE;
					}
				} else {
					interpreter.eval(
							"result = " + parentInput.findElement(By.xpath("..")).getAttribute("data-trick-min-value"));
					minValue = (int) interpreter.get("result");
					if (parentInput.findElement(By.xpath("..")).getAttribute("data-trick-max-value") != null) {
						interpreter.eval("result = "
								+ parentInput.findElement(By.xpath("..")).getAttribute("data-trick-max-value"));
						maxValue = (int) interpreter.get("result");
					} else {
						maxValue = Integer.MAX_VALUE;
					}
				}
				assert maxValue >= minValue;
			} catch (Exception e1) {
				minValue = 1;
				maxValue = 20;
			}

			do {
				newValue = new Random().nextInt(maxValue - minValue + 1) + minValue + "";
				sendKeys(input.getElement(), newValue);
			} while (newValue.equals(oldvalue) && (Math.abs(maxValue - minValue) > 1));

		} else if (parentInput.getAttribute("data-trick-field-type").equals("string")) {

			if (parentInput.getAttribute("data-trick-field").equals("impactFin")) {
				newValue = acronyms.get(new Random().nextInt(acronyms.size()));
			} else if (oldvalue.equals("Test")) {
				newValue = "Testa";
			} else {
				newValue = "Test";
			}

			sendKeys(input.getElement(), newValue);

		} else {
			throw new RuntimeException("Data type unknown");

		}
		return newValue;
	}

	private String dataFromXML(String section, int index, NeverStaleWebElement possibleInput, String xpathInput,
			NeverStaleWebElement input) throws XPathExpressionException, InterruptedException {
		String newValue = getData(section, index);
		NeverStaleWebElement parentInput = new NeverStaleWebElement(getDriver(), By.xpath(xpathInput + "/.."));

		if (parentInput.getTagName().equals("span")) {
			parentInput = new NeverStaleWebElement(getDriver(),
					By.xpath(xpathInput + "//ancestor::*[@data-trick-field-type]"));
		}

		if (input.getTagName().equals("select")) {
			new Select(input).selectByValue(newValue);
		} else if ((parentInput.getAttribute("data-trick-field-type").equals("double"))
				|| (parentInput.getAttribute("data-trick-field-type").equals("string"))) {
			sendKeys(input.getElement(), newValue);
		} else {
			throw new RuntimeException("Data type unknown");
		}
		return newValue;
	}

	private static Document getDocument(String filename) {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(filename));
		} catch (Exception e) {
			System.out.println("Error reading configuration file:");
			System.out.println(e.getMessage());
		}
		return null;
	}

	private String getData(String section, int index) throws XPathExpressionException {
		Document doc = getDocument(System.getProperty("user.dir") + "/src/test/resources/data/analysisInput.xml");
		XPath xpath = XPathFactory.newInstance().newXPath();

		Node data = (Node) xpath.evaluate("(//tab[@id='" + section + "']//value)[" + index + "]", doc,
				XPathConstants.NODE);
		return data.getFirstChild().getNodeValue();
	}

	// scenario
	protected void addScenario(String name, String type, String description, Boolean isSelected,
			String[] applicablesAssetTypes, Double preventive, Double detective, Double limitative, Double reactive,
			Double intentional, Double accidental, Double environmental, Double internalThreat, Double externalThreat)
			throws InterruptedException {
		click(By.xpath("//a[contains(@onclick,'editScenario(undefined,true)')]"));

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

		click(By.xpath("//button[contains(@onclick,'saveScenario')]"));
	}

	// standard
	protected void fillStandard(String label, String description, Boolean isComputable, String standardTypeValue)
			throws InterruptedException {
		sendKeys(findElement(By.id("standard_label")), label);
		sendKeys(findElement(By.id("standard_description")), description);

		selectCheckBox(isComputable, By.id("standard_computable"));

		click(By.xpath("//input[@value='" + standardTypeValue + "']"));
	}

	// asset
	protected void addAsset(String name, String type, int value, Boolean isSelected, String comment,
			String hiddenComment) throws InterruptedException {
		click(By.xpath("//a[@onclick='return editAsset(undefined,true);']"));
		assert !findElement(By.xpath("//div[@id='addAssetModal']//input[@id='asset_selected']")).isSelected();

		fillAsset(name, type, value, isSelected, comment, hiddenComment);

		click(By.xpath("//div[@id='addAssetModal']//button[contains(@onclick,'saveAsset')]"));
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

	// tools
	protected void moveSlider(String idSlider, double percentage) {
		WebElement slider = getDriver()
				.findElement(By.xpath("//input[@id='" + idSlider + "']/../div[@class='slider slider-vertical']"));

		WebElement bullet = slider.findElement(By.className("slider-handle"));

		Actions move = new Actions(getDriver());
		move.dragAndDropBy(bullet, 0, (int) (-slider.getLocation().y + bullet.getLocation().y));
		move.dragAndDropBy(bullet, 0, (int) (-slider.getSize().getHeight() * percentage));
		move.perform();
	}

	protected synchronized void select(WebElement selectElement, int index) throws InterruptedException {
		Select select = new Select(selectElement);

		while (index != getIndexText(select, select.getOptions().get(index).getText())) {
			if (index < getIndexText(select, select.getOptions().get(index).getText())) {
				selectElement.sendKeys(Keys.ARROW_UP);
			} else {
				selectElement.sendKeys(Keys.ARROW_DOWN);
			}
		}

	}

	private int getIndexText(Select select, String text) {
		int index = 0;
		while (index < select.getOptions().size()) {
			if (!select.getOptions().get(index).getText().equals(text)) {
				index++;
			}
		}
		if (index >= select.getOptions().size() || !select.getOptions().get(index).getText().equals(text)) {
			index = -1;
		}
		return index;
	}

}
